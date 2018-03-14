/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2018-2018 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2018 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

import org.apache.camel.CamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class Main {

    public static void main(String[] args) throws Exception {
        final CamelContext context = new DefaultCamelContext();
        context.addRoutes( new RouteBuilder(context) {
            @Override
            public void configure() throws Exception {
                onException(Throwable.class)
                        .handled(true)
                        .log(LoggingLevel.ERROR, "Unexpected exception while processing event in route ID ${routeId}; not processing with any later processors")
                        .log(LoggingLevel.ERROR, "${exception.stacktrace}");

                from("netty4:tcp://localhost:5817?receiveBufferSize=2147483647&sync=true&allowDefaultCodec=false")
                        .log("Receiving OpenNMS event over TCP port 5817")
                        .process(new NoOpProcessor());
            }
        });
        context.start();
    }
}
