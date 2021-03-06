/*
 * Sonar Erlang Plugin
 * Copyright (C) 2012 Tamas Kende
 * kende.tamas@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.erlang;

import com.google.common.collect.ImmutableList;
import org.apache.commons.collections.ListUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.FileLinesContext;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.scan.filesystem.FileQuery;
import org.sonar.api.scan.filesystem.ModuleFileSystem;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ErlangSquidSensorTest {

  private ErlangSquidSensor sensor;
  private ModuleFileSystem fileSystem;

  @Before
  public void setUp() {
    FileLinesContextFactory fileLinesContextFactory = mock(FileLinesContextFactory.class);
    FileLinesContext fileLinesContext = mock(FileLinesContext.class);
    when(fileLinesContextFactory.createFor(Mockito.any(Resource.class))).thenReturn(fileLinesContext);
    fileSystem = mock(ModuleFileSystem.class);
    when(fileSystem.sourceCharset()).thenReturn(Charset.forName("UTF-8"));
    sensor = new ErlangSquidSensor(mock(RulesProfile.class), fileSystem, null);
  }

  @Test
  public void should_execute_on_erlang_project() {
    Project project = new Project("key");
    ModuleFileSystem fs = mock(ModuleFileSystem.class);
    ErlangSquidSensor erlangSensor = new ErlangSquidSensor(mock(RulesProfile.class), fs, null);

    when(fs.files(Mockito.any(FileQuery.class))).thenReturn(ListUtils.EMPTY_LIST);
    assertThat(erlangSensor.shouldExecuteOnProject(project)).isFalse();

    when(fs.files(Mockito.any(FileQuery.class))).thenReturn(ImmutableList.of(new File("/tmp")));
    assertThat(erlangSensor.shouldExecuteOnProject(project)).isTrue();
  }

  @Test
  public void should_analyse() {
    when(fileSystem.files(Mockito.any(FileQuery.class))).thenReturn(Arrays.asList(
      new File("src/test/resources/cpd/person.erl")));

    Project project = new Project("key");
    ProjectUtil.addProjectFileSystem(project, "src/test/resources/cpd/");
    SensorContext context = mock(SensorContext.class);

    sensor.analyse(project, context);

    verify(context).saveMeasure(Matchers.any(Resource.class), Matchers.eq(CoreMetrics.FILES), Matchers.eq(1.0));
    verify(context).saveMeasure(Matchers.any(Resource.class), Matchers.eq(CoreMetrics.LINES), Matchers.eq(19.0));
    verify(context).saveMeasure(Matchers.any(Resource.class), Matchers.eq(CoreMetrics.NCLOC), Matchers.eq(14.0));
    verify(context).saveMeasure(Matchers.any(Resource.class), Matchers.eq(CoreMetrics.FUNCTIONS), Matchers.eq(2.0));
    verify(context).saveMeasure(Matchers.any(Resource.class), Matchers.eq(CoreMetrics.STATEMENTS), Matchers.eq(8.0));
    verify(context).saveMeasure(Matchers.any(Resource.class), Matchers.eq(CoreMetrics.COMPLEXITY), Matchers.eq(6.0));
    verify(context).saveMeasure(Matchers.any(Resource.class), Matchers.eq(CoreMetrics.COMMENT_LINES), Matchers.eq(1.0));
  }

  @Test
  public void analyse() {
    when(fileSystem.files(Mockito.any(FileQuery.class))).thenReturn(Arrays.asList(
      new File("src/test/resources/megaco_ber_bin_encoder.erl")));

    Project project = new Project("key");
    ProjectUtil.addProjectFileSystem(project, "src/test/resources/");
    SensorContext context = mock(SensorContext.class);

    sensor.analyse(project, context);

    verify(context).saveMeasure(Matchers.any(Resource.class), Matchers.eq(CoreMetrics.FILES), Matchers.eq(1.0));
    verify(context).saveMeasure(Matchers.any(Resource.class), Matchers.eq(CoreMetrics.LINES), Matchers.eq(717.0));
    verify(context).saveMeasure(Matchers.any(Resource.class), Matchers.eq(CoreMetrics.NCLOC), Matchers.eq(371.0));
    verify(context).saveMeasure(Matchers.any(Resource.class), Matchers.eq(CoreMetrics.FUNCTIONS), Matchers.eq(10.0));
    verify(context).saveMeasure(Matchers.any(Resource.class), Matchers.eq(CoreMetrics.STATEMENTS), Matchers.eq(210.0));
    verify(context).saveMeasure(Matchers.any(Resource.class), Matchers.eq(CoreMetrics.COMPLEXITY), Matchers.eq(90.0));
    verify(context).saveMeasure(Matchers.any(Resource.class), Matchers.eq(CoreMetrics.COMMENT_LINES), Matchers.eq(261.0));
  }

}
