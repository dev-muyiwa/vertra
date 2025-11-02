rootProject.name = "vertra"

include(
    "domain",
    "application",
    "infrastructure",
    "adapters:web",
    "adapters:persistence"
)
project(":adapters:web").projectDir = file("adapters/web")
project(":adapters:persistence").projectDir = file("adapters/persistence")
