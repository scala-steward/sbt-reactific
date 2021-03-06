/*
 * Copyright 2015-2017 Reactific Software LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.reactific.sbt

import sbt._
import sbt.Keys._
import org.scalastyle.sbt.ScalastylePlugin
import org.scalastyle.sbt.ScalastylePlugin.autoImport._

/** Unit Tests For Scalastyle */
object ScalastyleHelper extends AutoPluginHelper {

  /** The AutoPlugins that we depend upon */
  override def autoPlugins: Seq[AutoPlugin] = {
    Seq(ScalastylePlugin)
  }

  /** The configuration function to enable Scalastyle command, configuration
   * from the project directory, and the default config url.
   */
  def enable(project: Project): Project = {
    import ReactificPlugin.autoImport._
    val compName = "scalastyle-config.xml"
    val testName = "scalastyle-test-config.xml"
    project
      .enablePlugins(ScalastylePlugin)
      .settings(
        Seq(
          checkScalaStyle := true,
          scalastyleFailOnError := true,
          scalastyleConfig := baseDirectory.value / "project" / compName,
          (scalastyleConfig in Test) := baseDirectory.value / "project" / testName,
          scalastyleConfigUrl :=
            Some(
              url(
                "https://raw.githubusercontent.com/reactific/public" +
                  "/master/scalastyle-config.xml"
              )
            ),
          (scalastyleConfigUrl in Test) :=
            Some(
              url(
                "https://raw.githubusercontent.com/reactific/public" +
                  "/master/scalastyle-test-config.xml"
              )
            ),
          scalastyleConfigUrlCacheFile := compName,
          (scalastyleConfigUrlCacheFile in Test) := testName
        )
      )
  }
}
