import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

val moduleArtifactId = "SolanaKit"
val xcf = XCFramework(moduleArtifactId)

kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        macosX64(),
        macosArm64()
    ).forEach {
        it.binaries.framework {
            export(libs.web3.solana)
            export(libs.rpc.solana)

            baseName = moduleArtifactId

            binaryOption("bundleId", "team.solshare.${moduleArtifactId}")
            xcf.add(this)
            isStatic = true
        }
    }

    sourceSets {
        appleMain.dependencies {
            api(libs.web3.solana)
            api(libs.rpc.solana)
        }
    }
}