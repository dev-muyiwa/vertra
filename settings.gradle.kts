rootProject.name = "vertra"

include(
    "domain",
    "application",
    "infrastructure",
    "adapters:web",
    "adapters:persistence",
    "adapters:security",
    "adapters:audit"
)
project(":adapters:web").projectDir = file("adapters/web")
project(":adapters:persistence").projectDir = file("adapters/persistence")
project(":adapters:security").projectDir = file("adapters/security")
project(":adapters:audit").projectDir = file("adapters/audit")
