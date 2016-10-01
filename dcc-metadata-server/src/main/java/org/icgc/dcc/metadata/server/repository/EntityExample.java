/*
 * Copyright (c) 2016 The Ontario Institute for Cancer Research. All rights reserved.                             
 *                                                                                                               
 * This program and the accompanying materials are made available under the terms of the GNU Public License v3.0.
 * You should have received a copy of the GNU General Public License along with                                  
 * this program. If not, see <http://www.gnu.org/licenses/>.                                                     
 *                                                                                                               
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY                           
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES                          
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT                           
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,                                
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED                          
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;                               
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER                              
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN                         
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.icgc.dcc.metadata.server.repository;

import static lombok.AccessLevel.PRIVATE;
import static org.icgc.dcc.common.core.json.Jackson.DEFAULT;
import static org.springframework.data.domain.ExampleMatcher.matching;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.icgc.dcc.metadata.server.model.Entity;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import com.google.common.collect.ImmutableList;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.val;

/**
 * {@link Example} factory for {@link Entity}s.
 */
@NoArgsConstructor(access = PRIVATE)
public class EntityExample {

  /**
   * Constants.
   */
  @Getter(lazy = true, value = PRIVATE)
  private static final List<Field> fields = ImmutableList.copyOf(Entity.class.getDeclaredFields());

  public static Example<Entity> of(@NonNull Map<String, String> params) {
    return Example.of(createProbe(params), createMatcher(params));
  }

  private static Entity createProbe(Map<String, String> params) {
    return DEFAULT.convertValue(params, Entity.class);
  }

  private static ExampleMatcher createMatcher(Map<String, String> params) {
    ExampleMatcher matcher = matching();
    for (val field : getFields()) {
      val propertyPath = field.getName();

      // Skip matching fields that were not requested
      val requested = params.containsKey(propertyPath);
      if (requested) continue;

      matcher = matcher.withIgnorePaths(propertyPath);
    }

    return matcher;
  }

}
