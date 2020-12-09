/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.ufg.reconface.controller;

import br.ufg.reconface.service.FacialRecognitionService;
import br.ufg.reconface.service.StorageService;
import br.ufg.reconface.util.CoreMessage;
import br.ufg.reconface.util.InternalCode;
import br.ufg.reconface.util.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.io.IOException;

@RestController
@RequestMapping("/users")
public class Users {

    private final StorageService storageService;
    private final FacialRecognitionService facialRecognitionService;

    public Users(StorageService storageService, FacialRecognitionService facialRecognitionService){
        this.storageService = storageService;
        this.facialRecognitionService = facialRecognitionService;
    }

    @GetMapping("/{id}/hasFace")
    public ResponseEntity hasFaceRegistered(@PathVariable Long id){
        return storageService.hasClassifier(id) ?
                new ResponseEntity(new Response(id, CoreMessage.MS_CADASTRO_02), HttpStatus.OK ):
                new ResponseEntity(new Response(-1, CoreMessage.ME_CADASTRO_02), HttpStatus.OK);
    }

    @GetMapping("/{id}/getRegisteredFace")
    public String getUserRegisteredImage(@PathVariable Long id ){
        return this.storageService.getUserRegisteredFace(id);
    }

    @PostMapping("/{id}/file")
    public ResponseEntity uploadPhoto(@NotBlank(message = "invalid!") @RequestParam MultipartFile file, @PathVariable Long id) throws IOException {
        if(!file.isEmpty())
            return new ResponseEntity<>(storageService.store(file.getBytes(), id), HttpStatus.OK);

        else
            return new ResponseEntity<>(new Response(-1, CoreMessage.ME_UPLOAD_01), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/{id}/auth")
    public ResponseEntity auth(@RequestParam MultipartFile file, @PathVariable Long id) {
        if( !file.isEmpty()) {
            try {
                long userFound = facialRecognitionService.recognize(file.getBytes(), id);

                if (userFound > 0)
                    return new ResponseEntity<>(new Response(userFound, InternalCode.OK, CoreMessage.MS_RECON_01), HttpStatus.OK);
                else
                    return new ResponseEntity(new Response(userFound, InternalCode.MISMATCH, CoreMessage.ME_RECON_01), HttpStatus.OK);
            }catch(Exception e){
                return new ResponseEntity(new Response(-1, e.getMessage(), CoreMessage.ME_RECON_02), HttpStatus.OK);
            }
        }
        else
            return new ResponseEntity<>( new Response(-1, InternalCode.BAD_UPLOAD, CoreMessage.ME_UPLOAD_01), HttpStatus.BAD_REQUEST);
    }



}
