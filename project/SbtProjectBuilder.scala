/**********************************************************************************************************************
 *                                                                                                                    *
 * Copyright (c) 2015, Reactific Software LLC. All Rights Reserved.                                                   *
 *                                                                                                                    *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance     *
 * with the License. You may obtain a copy of the License at                                                          *
 *                                                                                                                    *
 *     http://www.apache.org/licenses/LICENSE-2.0                                                                     *
 *                                                                                                                    *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed   *
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for  *
 * the specific language governing permissions and limitations under the License.                                     *
 **********************************************************************************************************************/

import com.typesafe.sbt.pgp.PgpKeys
import sbt._
import sbt.Keys._

import java.io.File

import sbt.mavenint.PomExtraDependencyAttributes
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._
import xerial.sbt.Sonatype

object SbtPluginBuilder extends Build {
  lazy val standardResolvers = Seq(
    "BinTray-sbt" at "https://dl.bintray.com/sbt/sbt-plugin-releases",
    "BinTray-Typesafe" at "https://dl.bintray.com/typesafe/ivy-releases",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
    "jgit-repo" at "http://download.eclipse.org/jgit/maven"
  )

  val scalaV = "2.10"
  val sbtV = "0.13"

  def pluginModuleID(m: ModuleID) : ModuleID = {
    m.extra(PomExtraDependencyAttributes.SbtVersionKey -> sbtV,
      PomExtraDependencyAttributes.ScalaVersionKey -> scalaV)
      .copy(crossVersion = CrossVersion.Disabled)
  }

  val sonatype = publishTo := Some("Sonatype Snapshots Nexus" at "https://oss.sonatype.org/content/repositories/snapshots")


  lazy val project = Project("sbt-project", new File("."))
    .enablePlugins(Sonatype)
    .settings(
      sbtPlugin       := true,
      organization    := "com.reactific",
      scalaVersion    := "2.10.5",
      scalacOptions   ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint"),
      logLevel        := Level.Info,
      resolvers       ++= standardResolvers,

      // Scripted - sbt plugin tests
      ScriptedPlugin.scriptedSettings,
      ScriptedPlugin.scriptedLaunchOpts := { ScriptedPlugin.scriptedLaunchOpts.value ++
        Seq("-Xmx1024M", "-XX:MaxPermSize=256M", "-Dplugin.version=" + version.value)
      },
      ScriptedPlugin.scriptedBufferLog := false,

      // Release process
      releaseUseGlobalVersion := false,
      releasePublishArtifactsAction := PgpKeys.publishSigned.value,
      releaseProcess := Seq[ReleaseStep](
        checkSnapshotDependencies,
        inquireVersions,
        runClean,
        runTest,
        releaseStepCommand("scripted"),
        setReleaseVersion,
        commitReleaseVersion,
        tagRelease,
        releaseStepCommand("packageBin"),
        publishArtifacts,
        setNextVersion,
        commitNextVersion,
        ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
        pushChanges
      ),

      // Libraries for the project we plug into
      libraryDependencies ++= Seq (
        "org.scalatest" %% "scalatest" % "2.2.4" % "test",
        pluginModuleID("com.eed3si9n" % "sbt-buildinfo" % "0.5.0"),
        pluginModuleID("com.eed3si9n" % "sbt-sh" % "0.1.0"),
        pluginModuleID("com.eed3si9n" % "sbt-unidoc" % "0.3.3"),
        pluginModuleID("com.etsy" % "sbt-compile-quick-plugin" % "0.5.3"),
        pluginModuleID("com.github.gseitz" % "sbt-release" % "1.0.1"),
        pluginModuleID("com.jsuereth" % "sbt-pgp" % "1.0.0"),
        pluginModuleID("com.typesafe.sbt" % "sbt-git" % "0.8.4"),
        pluginModuleID("com.typesafe.sbt" % "sbt-javaversioncheck" % "0.1.0"),
        pluginModuleID("com.typesafe.sbt" % "sbt-license-report" % "1.0.0"),
        pluginModuleID("com.typesafe.sbt" % "sbt-site" % "0.8.1"),
        pluginModuleID("de.heikoseeberger" % "sbt-header" % "1.5.0"),
        pluginModuleID("org.scoverage" % "sbt-coveralls" % "1.0.0"),
        pluginModuleID("org.scoverage" % "sbt-scoverage" % "1.0.4"),
        pluginModuleID("org.xerial.sbt" % "sbt-sonatype" % "0.5.0")
      )
    )
    .settings(
      // Publishing to sonatype
      Sonatype.SonatypeKeys.sonatypeProfileName := "com.reactific",
      publishMavenStyle := true,
      publishArtifact in Test := false,
      pomIncludeRepository := { _ => false },
      licenses := Seq("Apache2" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
      homepage := Some(new URL("https://github.com/reactific/" + normalizedName.value)),
      pomExtra in Global := {
        <scm>
          <url>git@github.com:reactific/sbt-project.git</url>
          <connection>scm:git:git@github.com:reactific/sbt-project.git</connection>
        </scm>
        <developers>
          <developer>
            <id>reid-spencer</id>
            <name>Reid Spencer</name>
            <url>https://github.com/reid-spencer</url>
          </developer>
        </developers>
      }
    )
}