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

package com.reactific.sbt

import com.reactific.sbt.settings._
import com.typesafe.sbt.GitPlugin
import sbt._

/** The ProjectPlugin to add to your projects so they share a common set of build characteristics. */
object ProjectPlugin extends ProjectPluginTrait

trait ProjectPluginTrait extends AutoPluginHelper {

  override def autoPlugins : Seq[AutoPlugin] = Seq(
    Commands, CompileQuick, settings.Compiler,
    GitPlugin, Miscellaneous,
    Site, Unidoc, BuildInfo, Header, Publishing, Release
  )
}






















