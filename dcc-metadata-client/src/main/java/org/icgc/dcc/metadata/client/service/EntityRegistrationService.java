/*
 * Copyright (c) 2015 The Ontario Institute for Cancer Research. All rights reserved.                             
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
package org.icgc.dcc.metadata.client.service;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;
import static org.icgc.dcc.metadata.core.http.Headers.ENTITY_ID_HEADER;
import static org.springframework.http.HttpStatus.CONFLICT;

import org.icgc.dcc.metadata.client.manifest.Manifest.ManifestEntry;
import org.icgc.dcc.metadata.client.model.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EntityRegistrationService {

  @Value("${server.baseUrl}")
  private String baseUrl;
  @Autowired
  private RestTemplate restTemplate;
  @Autowired
  private RetryTemplate retryTemplate;

  public Entity register(@NonNull ManifestEntry file) {
    val entity = buildEntity(file);

    try {
      log.info("Posting: {}", entity);
      val response = register(entity);
      log.info("Entity: {}", response);
      return response;
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == CONFLICT) {
        return resolveEntityId(entity, e.getResponseHeaders());
      }
      log.error("Unexpected response code {} creating entity {}", e.getStatusCode(), entity);
      throw e;
    }
  }

  Entity buildEntity(ManifestEntry file) {
    val fname = scrubFileName(file.getFileName());
    if (StringUtils.isEmpty(fname)) {
      val msg = String.format("Empty file name specified for Manifest Entry in bundle %s", file.getGnosId());
      log.error(msg);
      throw new IllegalArgumentException(msg);
    }

    return new Entity()
        .setGnosId(file.getGnosId())
        .setProjectCode(file.getProjectCode())
        .setFileName(scrubFileName(file.getFileName()))
        .setAccess(file.getAccess());
  }

  @SneakyThrows
  private Entity register(Entity entity) {
    val url = baseUrl + "/" + "entities";
    return retryTemplate.execute(context -> restTemplate.postForEntity(url, entity, Entity.class).getBody());
  }

  private static Entity resolveEntityId(Entity entity, HttpHeaders responseHeaders) {
    val entityId = parseEntityId(responseHeaders);
    checkState(!isNullOrEmpty(entityId), "The server reported that %s already exists, but did not provide its ID",
        entity);
    entity.setId(entityId);
    log.info("The entity is aready registered. Reusing ID '{}'", entity.getId());

    return entity;
  }

  private static String parseEntityId(HttpHeaders responseHeaders) {
    val values = responseHeaders.get(ENTITY_ID_HEADER);
    checkState(values.size() == 1, "Malformed response. %s", values);

    return values.get(0);
  }

  /**
   * The manifests being generated need local path information to actually find the reference file. However, the
   * Metadata Server must absolutely <i>not</i> use the paths as it will affect how the object id (UUID) is generated.
   * 
   * @param fileName - will be interpreted as a Path
   * @return last part of the supplied path to a file
   */
  private static String scrubFileName(String fileName) {
    return StringUtils.getFilename(fileName);
  }
}
