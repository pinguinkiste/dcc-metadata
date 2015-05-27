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
package org.icgc.dcc.metadata.server.resource;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import javax.validation.Valid;

import lombok.val;

import org.icgc.dcc.metadata.server.model.Entity;
import org.icgc.dcc.metadata.server.repository.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

@RestController
@RequestMapping("/entities")
public class EntityResource {

  @Autowired
  EntityRepository repository;

  @RequestMapping("/{id}")
  public ResponseEntity<Entity> get(@PathVariable("id") String id) {
    val entity = repository.findOne(id);

    if (entity == null) {
      return new ResponseEntity<Entity>(NOT_FOUND);
    }

    return ResponseEntity.ok(entity);
  }

  @RequestMapping
  public ResponseEntity<List<Entity>> find(@RequestParam("gnosId") String gnosId,
      @RequestParam("fileName") String fileName) {
    List<Entity> entities = Lists.newArrayList();
    if (isNullOrEmpty(gnosId) && isNullOrEmpty(fileName)) {
      entities = repository.findAll();
    } else if (isNullOrEmpty(gnosId)) {
      entities = repository.findByFileName(fileName);
    } else if (isNullOrEmpty(fileName)) {
      entities = repository.findByGnosId(gnosId);
    } else {
      entities = repository.findByGnosIdAndFileName(gnosId, fileName);
    }

    return ResponseEntity.ok(entities);
  }

  @RequestMapping(value = "/{id}", method = HEAD)
  public ResponseEntity<?> exists(@PathVariable("id") String id) {
    val exists = repository.exists(id);

    if (!exists) {
      return new ResponseEntity<>(NOT_FOUND);
    }

    return ResponseEntity.ok(null);
  }

  @RequestMapping(method = POST)
  public ResponseEntity<Entity> save(@RequestBody @Valid Entity entity) {
    val existing = repository.findByGnosIdAndFileName(entity.getGnosId(), entity.getFileName());
    if (!existing.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    return ResponseEntity.ok(repository.save(entity));
  }

}
