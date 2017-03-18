/*
 * Copyright (c) 2016 Google Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.cloud.tasks.pullqueues;

import static com.google.common.truth.Truth.assertThat;

import com.example.cloud.tasks.Common;

import com.google.api.services.cloudtasks.v2beta2.CloudTasks;
import com.google.api.services.cloudtasks.v2beta2.model.Queue;
import com.google.api.services.cloudtasks.v2beta2.model.Task;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

/** Integration tests for the Cloud Tasks snippets. */
@SuppressWarnings("checkstyle:abbreviationaswordinname")
public class CloudTasksPullSnippetsIT {

  /** The Google Cloud project ID of the project created for the integration tests. */
  public static final String TEST_PROJECT_ID = System.getenv("GOOGLE_CLOUD_PROJECT");

  /** The project ID of the project created for the integration tests. */
  public static final String TEST_LOCATION_ID = System.getenv("LOCATION_ID");

  /** Cloud Tasks pull queue samples to run through integration tests. */
  private CloudTasksPullQueueSnippets snippets;

  @Before
  public void setUp() throws Exception {
    CloudTasks client = Common.authenticate();
    snippets = new CloudTasksPullQueueSnippets(client, TEST_PROJECT_ID, TEST_LOCATION_ID);
  }

  /** Integration tests that tests getting list of queues returns results. */
  @Test
  public void testListQueues() throws Exception {
    List<Queue> results = snippets.listQueues();
    assertThat(results.size()).isGreaterThan(0);
  }

  /**
   * Integration test verifying task creation works.
   *
   * @throws Exception On error
   */
  @Test
  public void testCreateTask() throws Exception {
    Queue queue = snippets.listQueues().get(0);
    Task task = snippets.createTask(queue);
    assertThat(task.getName()).contains(queue.getName());
  }

  /**
   * Integration test verifying task pulling and acknowledging works without an exception.
   *
   * @throws Exception On error
   */
  @Test
  public void testPullAndAckTask() throws Exception {
    Queue queue = snippets.listQueues().get(0);
    Task task = snippets.pullTask(queue);
    snippets.acknowledgeTask(task);
  }

  @Test
  public void testMainRunner() throws Exception {
    String[] args = {TEST_PROJECT_ID, TEST_LOCATION_ID};
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    snippets.runner(args, new PrintStream(os));
    String output = os.toString();
    assertThat(output).contains("Created task");
    assertThat(output).contains("Pulled task");
    assertThat(output).contains("Acknowledged task");
  }
}
