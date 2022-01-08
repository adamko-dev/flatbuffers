@Suppress("UnstableApiUsage") // Central declaration of repositories is an incubating feature
dependencyResolutionManagement {

//  repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

  repositories {
    mavenCentral()
    gradlePluginPortal()

    // Declare the Node.js and Yarn repositories
    ivy("https://nodejs.org/dist/") {
      name = "Node Distributions at $url"
      patternLayout { artifact("v[revision]/[artifact](-v[revision]-[classifier]).[ext]") }
      metadataSources { artifact() }
      content { includeModule("org.nodejs", "node") }
    }
    ivy("https://github.com/yarnpkg/yarn/releases/download") {
      name = "Yarn Distributions at $url"
      patternLayout { artifact("v[revision]/[artifact](-v[revision]).[ext]") }
      metadataSources { artifact() }
      content { includeModule("com.yarnpkg", "yarn") }
    }
  }

  pluginManagement {
    repositories {
      gradlePluginPortal()
      mavenCentral()
    }
  }
}
