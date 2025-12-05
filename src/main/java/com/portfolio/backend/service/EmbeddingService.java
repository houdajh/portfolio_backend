package com.portfolio.backend.service;

import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.Map;

@Service
public class EmbeddingService {

    private final OrtEnvironment env;
    private final OrtSession session;
    private final HuggingFaceTokenizer tokenizer;

    public EmbeddingService() throws Exception {

        env = OrtEnvironment.getEnvironment();

        session = env.createSession(
                "src/main/resources/models/all-MiniLM-L6-v2/model.onnx",
                new OrtSession.SessionOptions()
        );

        tokenizer = HuggingFaceTokenizer.newInstance(
                Paths.get("src/main/resources/models/all-MiniLM-L6-v2/tokenizer.json")
        );
    }

    public float[] embed(String text) throws Exception {

        var encoding = tokenizer.encode(text);

        long[] inputIds = encoding.getIds();
        long[] attentionMask = encoding.getAttentionMask();

        long[][] inputIds2D = new long[][]{inputIds};
        long[][] attentionMask2D = new long[][]{attentionMask};
        long[][] tokenType2D = new long[][]{new long[inputIds.length]};

        OrtSession.Result result = session.run(
                Map.of(
                        "input_ids", OnnxTensor.createTensor(env, inputIds2D),
                        "attention_mask", OnnxTensor.createTensor(env, attentionMask2D),
                        "token_type_ids", OnnxTensor.createTensor(env, tokenType2D)
                )
        );

        Object raw = result.get(0).getValue();

        // ---- CASE 1: Model returns [1, hidden_dim] → float[][] ----
        if (raw instanceof float[][] arr2d) {
            return arr2d[0];
        }

        // ---- CASE 2: Model returns [1, seq_len, hidden_dim] → float[][][] ----
        if (raw instanceof float[][][] arr3d) {
            float[][] seq = arr3d[0];   // shape = [seq_len, hidden_dim]

            int seqLen = seq.length;
            int dim = seq[0].length;

            float[] avg = new float[dim];

            // average pooling
            for (int t = 0; t < seqLen; t++) {
                for (int d = 0; d < dim; d++) {
                    avg[d] += seq[t][d];
                }
            }
            for (int d = 0; d < dim; d++) {
                avg[d] /= seqLen;
            }

            return avg;
        }

        throw new RuntimeException("Unsupported output format: " + raw.getClass());
    }


}
