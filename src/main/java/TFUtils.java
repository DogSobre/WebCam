
import org.tensorflow.*;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class TFUtils {

    Tensor executeSavedModel(String modelFolderPath, Tensor input) {
        try {
            Path path = Paths.get(ClassLoader.getSystemClassLoader().getResource(modelFolderPath).toURI()).toAbsolutePath();
            //Parse model, and read all bytes or exit
            SavedModelBundle model = SavedModelBundle.load(path.toString(), "serve");
            List<Tensor<?>> results = model.session().runner().feed("input", input).fetch("output").run();
            Tensor result = results.get(0);
            return result;
        } catch (URISyntaxException e) {
            throw new Error("invalid path");
        }
    }

    Tensor executeModelFromByteArray(byte[] graphDef, Tensor input) {
        try (Graph g = new Graph()) {
            g.importGraphDef(graphDef);
            try (Session s = new Session(g)) {
                Tensor result = s.runner().feed("input", input).fetch("output").run().get(0);
                return result;
            }
        }
    }

    float[][] tensorToArray(Tensor out) {
        Integer ELEM_RETURN = 1008;

        // Succeeds and prints "3"
        float[][] copy = new float[1][ELEM_RETURN];
        out.copyTo(copy);
        return copy;
    }


    HashMap fetchDescriptionAndValue(Tensor out) {
        float[][] copy = tensorToArray(out);
        Integer ELEM_RETURN = 1008;
        String[] descriptions = null;
        HashMap hm = new HashMap();
        Integer key = 0;
        Float value = new Float(0.0);
        for (int i = 0; i < ELEM_RETURN; i++) {
            if (i == 0) {
                key = i;
                value = copy[0][i];
            } else if (copy[0][i] > value) {
                key = i;
                value = copy[0][i];
            }
        }
        try {
            descriptions = convertFileToArray("inception5h/labels.txt");
            Integer percent = Math.round(value * 100);
            hm.put(descriptions[key], percent);
        } catch (IOException e) {
            System.out.println(e);
        }
        return hm;
    }


    HashMap fetchDescriptionAndPath(Tensor out, String pathName) {
        float[][] copy = tensorToArray(out);
        Integer ELEM_RETURN = 1008;
        String[] descriptions = null;
        HashMap hm = new HashMap();
        Integer key = 0;
        Float value = new Float(0.0);
        for (int i = 0; i < ELEM_RETURN; i++) {
            if (i == 0) {
                key = i;
                value = copy[0][i];
            } else if (copy[0][i] > value) {
                key = i;
                value = copy[0][i];
            }
        }
        try {
            descriptions = convertFileToArray("inception5h/labels.txt");
            hm.put(descriptions[key], pathName);
        } catch (IOException e) {
            System.out.println(e);
        }
        return hm;
    }

    public String[] convertFileToArray(String path) throws IOException {
        String[] arr = null;
        List<String> itemsSchool = new ArrayList<String>();


        FileInputStream fstream_school = new FileInputStream(path);
        DataInputStream data_input = new DataInputStream(fstream_school);
        BufferedReader buffer = new BufferedReader(new InputStreamReader(data_input));
        String str_line;

        while ((str_line = buffer.readLine()) != null) {
            str_line = str_line.trim();
            if ((str_line.length() != 0)) {
                itemsSchool.add(str_line);
            }
        }
        return (String[]) itemsSchool.toArray(new String[itemsSchool.size()]);
    }

    /**
     * create a Tensor from an image
     * <p>
     * Scale and normalize an image (to 224x224), and convert to a tensor
     *
     * @param imageBytes
     * @return
     */
    Tensor byteBufferToTensor(byte[] imageBytes) {

        try (Graph g = new Graph()) {
            GraphBuilder graphBuilder = new GraphBuilder(g);
            // Some constants specific to the pre-trained model at:
            // https://storage.googleapis.com/download.tensorflow.org/models/inception5h.zip
            //
            // - The model was trained with images scaled to 224x224 pixels.
            // - The colors, represented as R, G, B in 1-byte each were converted to
            //   float using (value - Mean)/Scale.
            final float mean = 117f;
            final float scale = 1f;
            final int H = 224;
            final int W = 224;

            // Since the graph is being constructed once per execution here, we can use a constant for the
            // input image. If the graph were to be re-used for multiple input images, a placeholder would
            // have been more appropriate.
            final Output input = graphBuilder.constant("input", imageBytes);
            final Output output =
                    graphBuilder.div(
                            graphBuilder.sub(
                                    graphBuilder.resizeBilinear(
                                            graphBuilder.expandDims(
                                                    graphBuilder.cast(graphBuilder.decodeJpeg(input, 3), DataType.FLOAT),
                                                    graphBuilder.constant("make_batch", 0)),
                                            graphBuilder.constant("size", new int[]{H, W})),
                                    graphBuilder.constant("mean", mean)),
                            graphBuilder.constant("scale", scale));
            try (Session s = new Session(g)) {
                return s.runner().fetch(output.op().name()).run().get(0);
            }
        }
    }

    private static class GraphBuilder {
        private Graph g;

        GraphBuilder(Graph g) {
            this.g = g;
        }

        Output div(Output x, Output y) {
            return binaryOp("Div", x, y);
        }

        Output sub(Output x, Output y) {
            return binaryOp("Sub", x, y);
        }

        Output resizeBilinear(Output images, Output size) {
            return binaryOp("ResizeBilinear", images, size);
        }

        Output expandDims(Output input, Output dim) {
            return binaryOp("ExpandDims", input, dim);
        }

        Output cast(Output value, DataType dtype) {
            return g.opBuilder("Cast", "Cast").addInput(value).setAttr("DstT", dtype).build().output(0);
        }

        Output decodeJpeg(Output contents, long channels) {
            return g.opBuilder("DecodeJpeg", "DecodeJpeg")
                    .addInput(contents)
                    .setAttr("channels", channels)
                    .build()
                    .output(0);
        }

        Output constant(String name, Object value) {
            try (Tensor t = Tensor.create(value)) {
                return g.opBuilder("Const", name)
                        .setAttr("dtype", t.dataType())
                        .setAttr("value", t)
                        .build()
                        .output(0);
            }
        }

        private Output binaryOp(String type, Output in1, Output in2) {
            return g.opBuilder(type, type).addInput(in1).addInput(in2).build().output(0);
        }
    }

}
