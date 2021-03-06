package server.ws;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//package websocket;

import java.util.HashSet;
import java.util.Set;

import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;

//import websocket.drawboard.DrawboardEndpoint;
//import websocket.echo.EchoEndpoint;

//public class DeployEndpoints { }
public class DeployEndpoints implements ServerApplicationConfig {

    @Override
    public Set<ServerEndpointConfig> getEndpointConfigs(
            Set<Class<? extends Endpoint>> scanned) {

        Set<ServerEndpointConfig> result = new HashSet<>();

        /*if (scanned.contains(EchoEndpoint.class)) {
            result.add(ServerEndpointConfig.Builder.create(
                    EchoEndpoint.class,
                    "/websocket/echoProgrammatic").build());
        }*/

        /*if (scanned.contains(DrawboardEndpoint.class)) {
            result.add(ServerEndpointConfig.Builder.create(
                    DrawboardEndpoint.class,
                    "/websocket/drawboard").build());
        }*/

        return result;
    }


    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
        // Was:
    	// Deploy all WebSocket endpoints defined by annotations in the examples
        // web application. Filter out all others to avoid issues when running
        // tests on Gump
    	// Now:
    	// Everything in the current package
        Set<Class<?>> results = new HashSet<>();
		System.out.println("Start Scan: *");
        for (Class<?> clazz : scanned) {
        	/*
        	 * This is the magic thing...
        	 * In examples this was "websocket."
        	 */
        	String sCurrentName=clazz.getPackage().getName();
    		System.out.println("Scanning:" + sCurrentName);
            if (clazz.getPackage().getName().startsWith("server.")) {
            	String current=clazz.getPackage().getName();
        		System.out.println("Making websocket visible:" + current);
                results.add(clazz);
            }
        }
        return results;
    }
}

