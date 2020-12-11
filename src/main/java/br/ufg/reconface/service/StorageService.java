/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.ufg.reconface.service;


import br.ufg.reconface.util.CoreMessage;
import br.ufg.reconface.util.Response;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;

@Service
public class StorageService {

    private static Logger logger = LoggerFactory.getLogger(StorageService.class);

    private final Path fileStorageLocation;

    private final FacialRecognitionService facialRecognitionService;

    @Value("${user.face.filename}")
    private String defaultPhotoName;

    @Value("${user.face.file.extension}")
    private String defaultExtension;


    @Autowired
    public StorageService(AppPropertiesService appPropertiesService, FacialRecognitionService facialRecognitionService ) {

        this.facialRecognitionService = facialRecognitionService;


        this.fileStorageLocation = Paths.get(appPropertiesService.getStorageDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public String getUserRegisteredFace(long id) {
        try {
                Path path = fileStorageLocation.resolve(String.valueOf(id)).resolve(defaultPhotoName + "." + defaultExtension);
                byte[] imageBytes = Files.readAllBytes(path);
                return Base64.encodeBase64String(imageBytes);
        }
        catch(Exception e){
            System.out.println("Failed to retrieve image: " + e.getMessage());
            return null;
        }
    }

    public boolean hasClassifier(long userId){
        return Files.exists(fileStorageLocation.resolve(facialRecognitionService.getClassifierName(userId)));
    }

    public Response store(byte[] bytes, long userId) throws IOException {
        Path userFolder = this.fileStorageLocation.resolve(String.valueOf(userId));

        if(Files.notExists(userFolder))
            Files.createDirectories(userFolder);

        if (!facialRecognitionService.generateClassifier(bytes, userId))
            return new Response(-1, CoreMessage.ME_CADASTRO_01);

        return new Response(userId, CoreMessage.MS_CADASTRO_01);
    }

    public Path saveInTmp(byte[] bytes)  throws IOException {

        String filename = StringUtils.cleanPath("foto_".concat(String.valueOf(Calendar.getInstance().getTimeInMillis())).concat(".jpg"));

        Path targetLocation = Path.of("/tmp/").resolve(filename);

        Files.write(targetLocation, bytes);

        return targetLocation;
    }
}
