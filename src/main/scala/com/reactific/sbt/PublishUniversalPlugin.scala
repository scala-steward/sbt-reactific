package com.reactific.sbt

import com.reactific.sbt.settings._
import com.typesafe.sbt.SbtNativePackager.{Universal, UniversalDocs, UniversalSrc}
import com.typesafe.sbt.packager.SettingsHelper
import com.typesafe.sbt.packager.universal.UniversalPlugin
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport._
import sbt.Keys._
import sbt._

/** Enable PublishUniversalPlugin to make universal artifacts publishable */
object PublishUniversalPlugin extends AutoPluginHelper {

  override def autoPlugins : Seq[AutoPlugin] = Seq(
    ProjectPlugin, UniversalPlugin
  )

  /**
    * Define the values of the settings
    */
  override def projectSettings: Seq[Setting[_]] = {
    super.projectSettings ++
      SettingsHelper.makeDeploymentSettings(Universal, dist in Universal, "zip") ++
      SettingsHelper.makeDeploymentSettings(UniversalSrc, dist in UniversalSrc, "zip") ++
      SettingsHelper.makeDeploymentSettings(UniversalDocs, dist in UniversalDocs, "zip") ++ Seq(
      //--hard coded result of "universal:packageBin"
      packageZip :=
        (baseDirectory in Compile).value / "target" / "universal" / s"${name.value}-${version.value}.zip",
      //--label the zip artifact as a zip instead of the default jar
      artifact in (Universal, packageZip) ~= { (art:Artifact) =>
        art.copy(`type` = "zip", extension = "zip")
      },
      //--make sure the zip gets made before the publishing commands for the added artifacts
      publish <<= publish dependsOn (dist in Universal),
      publishM2 <<= publishM2 dependsOn (dist in Universal),
      publishLocal <<= publishLocal dependsOn (dist in Universal)
    ) ++
      //--add the artifact so it is included in the publishing tasks
      addArtifact(artifact in (Universal, packageZip), packageZip in Universal)
  }

  /** The Configurations to add to each project that activates this AutoPlugin.*/
  override def projectConfigurations: Seq[Configuration] = Nil
  override def buildSettings : Seq[Setting[_]] = Nil
  override def globalSettings: Seq[Setting[_]] = Nil
}
