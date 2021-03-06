/*
 * Copyright 2016-2018 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nebula.test

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner

abstract class IntegrationTestKitSpec extends BaseIntegrationSpec {
    static final String LINE_END = System.getProperty('line.separator')
    boolean keepFiles = false
    boolean debug
    File buildFile
    File settingsFile

    def setup() {
        settingsFile = new File(projectDir, "settings.gradle")
        buildFile = new File(projectDir, "build.gradle")
    }

    def cleanup() {
        if (!keepFiles) {
            projectDir.deleteDir()
        }
    }

    File addSubproject(String name, String buildGradle) {
        def subdir = new File(projectDir, name)
        subdir.mkdirs()

        settingsFile << "include \"${name}\"${LINE_END}"

        new File(subdir, "build.gradle").text = buildGradle

        return subdir
    }

    BuildResult runTasks(String... tasks) {
        BuildResult result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments(calculateArguments(tasks))
                .withDebug(debug)
                .withPluginClasspath()
                .forwardOutput()
                .build()
        return checkForDeprecations(result)
    }

    BuildResult runTasksAndFail(String... tasks) {
        BuildResult result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments(calculateArguments(tasks))
                .withDebug(debug)
                .withPluginClasspath()
                .forwardOutput()
                .buildAndFail()
        return checkForDeprecations(result)
    }

    protected BuildResult checkForDeprecations(BuildResult result) {
        checkForDeprecations(result.output)
        return result
    }
}
