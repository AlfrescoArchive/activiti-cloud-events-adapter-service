/*
 * Copyright 2018 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.cloud.events.adapter.transformers;

import org.activiti.runtime.api.event.CloudProcessCancelled;
import org.activiti.runtime.api.event.CloudProcessRuntimeEvent;
import org.activiti.runtime.api.event.ProcessRuntimeEvent.ProcessEvents;
import org.activiti.runtime.api.model.ProcessInstance;
import org.alfresco.event.model.EventV1;
import org.alfresco.event.model.activiti.ProcessResourceV1;
import org.springframework.stereotype.Component;

/**
 * @author Jamal Kaabi-Mofrad
 */
@Component
public class ProcessEventTransformer extends AbstractEventTransformer<CloudProcessRuntimeEvent, ProcessResourceV1> {

    private static final String[] SUPPORTED_EVENT_TYPES = { "PROCESS_CREATED", "PROCESS_STARTED", "PROCESS_COMPLETED",
                "PROCESS_CANCELLED", "PROCESS_SUSPENDED", "PROCESS_RESUMED" };

    public ProcessEventTransformer() {
        super(SUPPORTED_EVENT_TYPES);
    }

    @Override
    public EventV1<ProcessResourceV1> transform(CloudProcessRuntimeEvent event) {
        ProcessResourceV1 resource = new ProcessResourceV1(event.getId(), null);
        setCommonValues(event, resource);

        ProcessInstance entity = event.getEntity();
        resource.setStatus(entity.getStatus()
                    .name());
        resource.setProcessDefinitionId(entity.getProcessDefinitionId());
        resource.setProcessDefinitionKey(entity.getProcessDefinitionKey());

        if (event.getEventType() == ProcessEvents.PROCESS_CANCELLED) {
            resource.setCause(((CloudProcessCancelled) event).getCause());
        }

        return new EventV1<>(event.getEventType().name(), entity.getInitiator(), resource);
    }
}
