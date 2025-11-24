package com.vertra.adapters.web.controller;

import com.vertra.adapters.web.dto.request.auth.CompleteOAuthSetupRequest;
import com.vertra.adapters.web.dto.request.auth.OAuthCallbackRequest;
import com.vertra.adapters.web.dto.request.auth.RecoverDeviceRequest;
import com.vertra.adapters.web.dto.response.auth.OAuthCallbackResponse;
import com.vertra.adapters.web.dto.response.auth.OAuthSetupResponse;
import com.vertra.adapters.web.dto.response.common.ApiResponse;
import com.vertra.application.port.in.auth.CompleteOAuthSetupUseCase;
import com.vertra.application.port.in.auth.OAuthCallbackUseCase;
import com.vertra.application.port.in.auth.RecoverDeviceUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth/oauth")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthCallbackUseCase oAuthCallbackUseCase;
    private final CompleteOAuthSetupUseCase completeOAuthSetupUseCase;
    private final RecoverDeviceUseCase recoverDeviceUseCase;

    /**
     * Handle OAuth callback from provider.
     * Client sends the access token obtained from OAuth provider.
     *
     * Returns one of three responses:
     * - new_user: User needs to complete setup (generate keys on client)
     * - known_device: Login successful, returns tokens and encrypted private key
     * - recovery_required: New device needs recovery key to decrypt
     */
    @PostMapping("/{provider}/callback")
    public ResponseEntity<ApiResponse<OAuthCallbackResponse>> handleCallback(
            @PathVariable String provider,
            @Valid @RequestBody OAuthCallbackRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("OAuth callback received: provider={}", provider);

        var command = new OAuthCallbackUseCase.OAuthCallbackCommand(
                provider,
                request.accessToken(),
                request.deviceId(),
                request.deviceName(),
                request.deviceFingerprint(),
                getClientIp(httpRequest),
                httpRequest.getHeader("User-Agent")
        );

        var result = oAuthCallbackUseCase.execute(command);

        OAuthCallbackResponse response = switch (result) {
            case OAuthCallbackUseCase.OAuthCallbackResponse.NewUserResponse r ->
                    OAuthCallbackResponse.newUser(
                            r.temporaryToken(),
                            r.email(),
                            r.firstName(),
                            r.lastName(),
                            r.profilePictureUrl(),
                            r.provider().name().toLowerCase(),
                            r.providerId()
                    );
            case OAuthCallbackUseCase.OAuthCallbackResponse.KnownDeviceResponse r ->
                    OAuthCallbackResponse.knownDevice(
                            r.accessToken(),
                            r.refreshToken(),
                            r.expiresIn(),
                            r.encryptedPrivateKey(),
                            r.user().id(),
                            r.user().email(),
                            r.user().firstName(),
                            r.user().lastName()
                    );
            case OAuthCallbackUseCase.OAuthCallbackResponse.RecoveryRequiredResponse r ->
                    OAuthCallbackResponse.recoveryRequired(
                            r.temporaryToken(),
                            r.recoverySalt(),
                            r.email(),
                            r.firstName(),
                            r.lastName()
                    );
        };

        return ResponseEntity.ok(ApiResponse.success(response, "OAuth callback processed"));
    }

    /**
     * Complete OAuth setup for new users.
     * Client generates keypair, encrypts private key, and sends here.
     */
    @PostMapping("/complete-setup")
    public ResponseEntity<ApiResponse<OAuthSetupResponse>> completeSetup(
            @Valid @RequestBody CompleteOAuthSetupRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("Completing OAuth setup");

        var command = new CompleteOAuthSetupUseCase.CompleteOAuthSetupCommand(
                request.temporaryToken(),
                request.firstName(),
                request.lastName(),
                request.profilePictureUrl(),
                request.accountPublicKey(),
                request.deviceId(),
                request.deviceName(),
                request.deviceFingerprint(),
                request.encryptedPrivateKey(),
                request.recoveryEncryptedPrivateKey(),
                request.recoverySalt(),
                getClientIp(httpRequest),
                httpRequest.getHeader("User-Agent")
        );

        var result = completeOAuthSetupUseCase.execute(command);

        var response = new OAuthSetupResponse(
                result.accessToken(),
                result.refreshToken(),
                result.expiresIn(),
                new OAuthSetupResponse.UserInfo(
                        result.user().id(),
                        result.user().email(),
                        result.user().firstName(),
                        result.user().lastName(),
                        result.user().profilePictureUrl()
                )
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Account created successfully"));
    }

    /**
     * Recover access on a new device.
     * User has decrypted their private key using recovery key on client,
     * and sends newly encrypted private key for this device.
     */
    @PostMapping("/recover-device")
    public ResponseEntity<ApiResponse<OAuthSetupResponse>> recoverDevice(
            @Valid @RequestBody RecoverDeviceRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("Processing device recovery");

        var command = new RecoverDeviceUseCase.RecoverDeviceCommand(
                request.temporaryToken(),
                request.deviceId(),
                request.deviceName(),
                request.deviceFingerprint(),
                request.encryptedPrivateKey(),
                getClientIp(httpRequest),
                httpRequest.getHeader("User-Agent")
        );

        var result = recoverDeviceUseCase.execute(command);

        var response = new OAuthSetupResponse(
                result.accessToken(),
                result.refreshToken(),
                result.expiresIn(),
                new OAuthSetupResponse.UserInfo(
                        result.user().id(),
                        result.user().email(),
                        result.user().firstName(),
                        result.user().lastName(),
                        result.user().profilePictureUrl()
                )
        );

        return ResponseEntity.ok(ApiResponse.success(response, "Device recovered successfully"));
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
