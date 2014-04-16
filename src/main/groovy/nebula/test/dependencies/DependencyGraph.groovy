/*
 * Copyright 2014 Netflix, Inc.
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
package nebula.test.dependencies

class DependencyGraph {
    List<DependencyGraphNode> nodes = []

    DependencyGraph(List<String> graph) {
        graph.each { nodes << parseNode(it) }
    }

    DependencyGraph(String... graph) {
        this(graph as List)
    }
    
    private DependencyGraphNode parseNode(String s) {
        // Don't use tokenize, it'll make each character a possible delimeter, e.g. \t\n would tokenize on both
        // \t OR \n, not the combination of \t\n.
        def parts = s.split('->')
        def (group, artifact, version) = parts[0].trim().tokenize(':')
        def coordinate = new Coordinate(group: group, artifact: artifact, version: version)
        def dependencies = (parts.size() > 1) ? parseDependencies(parts[1]) : []

        new DependencyGraphNode(coordinate: coordinate, dependencies: dependencies)
    }

    private List<Coordinate> parseDependencies(String s) {
        List<Coordinate> dependencies = []
        s.tokenize('|').each { String dependency ->
            def (group, artifact, version) = dependency.trim().tokenize(':')
            dependencies << new Coordinate(group: group, artifact: artifact, version: version)
        }

        dependencies
    }
}
