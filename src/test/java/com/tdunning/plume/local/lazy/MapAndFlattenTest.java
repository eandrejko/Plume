/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tdunning.plume.local.lazy;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.tdunning.plume.DoFn;
import com.tdunning.plume.EmitFn;
import com.tdunning.plume.PCollection;

public class MapAndFlattenTest {

  /**
   * Deferred execution of ((1,2,3)+(4,5,6)) => x+1 
   */
  @Test
  public void test() {
    DoFn<Integer, Integer> plusOne = new DoFn<Integer, Integer>() {
      @Override
      public void process(Integer v, EmitFn<Integer> emitter) {
        emitter.emit(v + 1);
      }
    };
    List<Integer> l1 = Lists.newArrayList(1, 2, 3);
    List<Integer> l2 = Lists.newArrayList(4, 5, 6);
    // Get Plume runtime
    LazyPlume plume = new LazyPlume();
    PCollection<Integer> output = plume.flatten(plume.fromJava(l1), plume.fromJava(l2)).map(plusOne, null);
    // Get an executor
    Executor executor = new Executor();
    Iterable<Integer> result = executor.execute((LazyCollection<Integer>)output);
    List<Integer> l = Lists.newArrayList(result);
    Collections.sort(l);
    assertEquals(l.get(0).intValue(), 2);
    assertEquals(l.get(1).intValue(), 3);
    assertEquals(l.get(2).intValue(), 4);
    assertEquals(l.get(3).intValue(), 5); 
    assertEquals(l.get(4).intValue(), 6); 
    assertEquals(l.get(5).intValue(), 7);
  }
}
