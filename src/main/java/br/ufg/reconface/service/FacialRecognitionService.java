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
package br.ufg.reconface.service;

import br.ufg.reconface.util.InternalCode;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.IntBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

@Service
public class FacialRecognitionService {

    private final Path fileStorageLocation;
    private static final double TRESHOLD = 130D;

    @Value("${user.face.filename}")
    private String defaultPhotoName;

    @Value("${user.face.file.extension}")
    private String defaultExtension;

    @Autowired
    public FacialRecognitionService(AppPropertiesService appPropertiesService) {

        this.fileStorageLocation = Paths.get(appPropertiesService.getStorageDir())
                .toAbsolutePath().normalize();
    }

    String getClassifierName(long userId){
        return String.format("classificador-lbph-%d.yml", userId);
    }

    boolean generateClassifier(byte[] imageBytes, long userId) {
        Mat capturedFace = captureFace(imageBytes);
        if(capturedFace != null && !capturedFace.empty()) {

            Path rectTargetLocation = this.fileStorageLocation.resolve(String.valueOf(userId));
            String rectFileName = defaultPhotoName.concat(".").concat(defaultExtension);

            //salvando retangulo da imagem
            imwrite(rectTargetLocation.resolve(rectFileName).toString(), capturedFace);

            Path classifierPath = this.fileStorageLocation.resolve(getClassifierName(userId));
            System.out.println(classifierPath.toString());

            LBPHFaceRecognizer lbph = LBPHFaceRecognizer.create();

            lbph.setRadius(3);
            lbph.setNeighbors(12);
            lbph.setGridX(8);
            lbph.setGridY(8);
            lbph.setThreshold(TRESHOLD);

            Mat label = new Mat(1, 1, CV_32SC1);

            IntBuffer labelsBuffer = label.createBuffer();
            labelsBuffer.put(0, Integer.parseInt(String.valueOf(userId)));

            MatVector photos = new MatVector(1);

            photos.put(0, capturedFace);

            lbph.train(photos, label);
            lbph.write(classifierPath.toUri().getPath());
            lbph.close();

            return true;
        }
        else
            return false;
    }


    public long recognize(byte[] imageBytes, long userId) throws Exception{

        Mat capturedFace = captureFace(imageBytes);

        if (Optional.ofNullable(capturedFace).isEmpty()) {
            throw new Exception(InternalCode.NULL_FACE);
        }

        FaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();

        Path classifierPath = this.fileStorageLocation.resolve(getClassifierName(userId));

        try {
            faceRecognizer.read(classifierPath.toUri().getPath());
        } catch (Exception e) {
            throw new Exception(InternalCode.NO_CLASSIFIER);
        }

        faceRecognizer.setThreshold(TRESHOLD);

        IntPointer label = new IntPointer(1);
        DoublePointer confidence = new DoublePointer(1);

        faceRecognizer.predict(capturedFace, label, confidence);

        faceRecognizer.close();

        int predict = label.get(0);

        return predict;
    }

    private Mat captureFace(byte[] imageBytes){
        Mat imageGray = new Mat();
        Mat imageColor = imdecode(new Mat(imageBytes), IMREAD_UNCHANGED);

        cvtColor(imageColor, imageGray, COLOR_BGRA2GRAY);

        RectVector detectedFace = new RectVector();

        CascadeClassifier detectorFace = new CascadeClassifier("src/main/resources/haarcascade/haarcascade-frontalface-alt.xml");
        detectorFace.detectMultiScale(imageGray,
                detectedFace,
                1.1,
                1,
                0,
                new Size(40, 40),
                new Size(1900, 1900));

        detectorFace.close();

        if(detectedFace.size() > 0){
            Rect dataFace = detectedFace.get(0);

            rectangle(imageColor, dataFace, new Scalar(0, 0, 255, 0));

            Mat capturedFace = new Mat(imageGray, dataFace);
            resize(capturedFace, capturedFace, new Size(160, 160));

            return capturedFace;
        }
        else
            return null;
    }

}
