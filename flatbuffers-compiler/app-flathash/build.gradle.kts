plugins {
  id("flatbuffers.language.cpp")
  `cpp-application`
}

application {
  baseName.set("flathash")

  targetMachines.set(
    listOf(
      machines.windows.x86,
      machines.windows.x86_64,
      machines.macOS.x86_64,
      machines.linux.x86_64
    )
  )
}
