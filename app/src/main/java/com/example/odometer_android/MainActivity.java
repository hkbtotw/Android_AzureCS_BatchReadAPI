package com.example.odometer_android;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import edmt.dev.edmtdevcognitivevision.Contract.AnalysisInDomainResult;
import edmt.dev.edmtdevcognitivevision.Contract.AnalysisResult;
import edmt.dev.edmtdevcognitivevision.Contract.HandwritingRecognitionOperation;
import edmt.dev.edmtdevcognitivevision.Contract.HandwritingRecognitionOperationResult;
import edmt.dev.edmtdevcognitivevision.Contract.Model;
import edmt.dev.edmtdevcognitivevision.Contract.ModelResult;
import edmt.dev.edmtdevcognitivevision.Contract.OCR;
import edmt.dev.edmtdevcognitivevision.Rest.VisionServiceException;
import edmt.dev.edmtdevcognitivevision.Rest.WebServiceRequest;
import edmt.dev.edmtdevcognitivevision.VisionServiceClient;
import edmt.dev.edmtdevcognitivevision.VisionServiceRestClient;
import edmt.dev.edmtdevcognitivevision.Utils.Utils;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button btnProcess;
    TextView txtResult;

    private final String API_KEY="";
    private final String API_LINK="https://southeastasia.api.cognitive.microsoft.com/vision/v2.0";
    VisionServiceClient visionServiceClient=new VisionServiceRestClient(API_KEY,API_LINK);
    VisionBatchRead visionBatchRead=new VisionBatchRead(API_KEY,API_LINK);

    String result = "";
    URL url;
    HttpURLConnection urlConnection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView=(ImageView) findViewById(R.id.image_view);
        btnProcess=(Button)findViewById(R.id.btn_process);
        txtResult=(TextView)findViewById(R.id.txt_result);

        //final Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.elephant);
        //final Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.text_test);
        final Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.test2);
        imageView.setImageBitmap(bitmap);

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                final ByteArrayInputStream inputStream=new ByteArrayInputStream(outputStream.toByteArray());

                AsyncTask<InputStream,String, String> visionTask=new AsyncTask<InputStream, String, String>() {
                    ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);

                    @Override
                    protected void onPreExecute(){
                        progressDialog.show();
                    }

                    @Override
                    protected String doInBackground(InputStream... inputStreams) {
                        try{
                            publishProgress("Recognizing...");
                            //String[] features={"Description"};
                            String[] features={};
                            String[] details={};
                            StringBuilder result=visionBatchRead.analyzeImage1(inputStreams[0],features,details);
                            Log.i("Result"," textview : "+result);
                            Log.i("Result"," textview : "+result.toString());


                            //AnalysisResult result=visionServiceClient.analyzeImage(inputStreams[0],features,details);
                            //OCR result=visionServiceClient.recognizeText(inputStreams[0],"en",true);
                            //String jsonResult=new Gson().toJson(result);
                            //Log.i("info","-->"+jsonResult);
                            return result.toString();

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (VisionServiceException e) {
                            e.printStackTrace();
                        }
                        return "";
                    }

                    @Override
                    protected void onPostExecute(String s){
                        if(TextUtils.isEmpty(s)){
                            Toast.makeText(MainActivity.this,"API return empty",Toast.LENGTH_SHORT).show();

                        }else {
                            progressDialog.dismiss();


                            /*
                            OCR result = new Gson().fromJson(s, OCR.class);
                            StringBuilder result_text = new StringBuilder();

                            Log.i("OCR"," ==> "+s);
                            int regionNumber= result.regions.size();
                            int lineNumer=result.regions.get(0).lines.size();
                            int wordNumber;

                            //Log.i("OCR"," ==> "+ result.regions.get(0).lines.get(0).words.size());
                            Log.i("OCR"," RegionNumber ==> "+ regionNumber);
                            Log.i("OCR"," LineNumber ==> "+ lineNumer);
                            for(int i=0; i<lineNumer; i++){

                                wordNumber=result.regions.get(0).lines.get(i).words.size();
                                Log.i("OCR"," wordNumber ==> "+ wordNumber+" "+i);
                                for(int j=0; j<wordNumber;j++){
                                    Log.i("OCR"," ==> "+ result.regions.get(0).lines.get(i).words.get(j).text+" "+j);
                                    result_text.append(result.regions.get(0).lines.get(i).words.get(j).text+" ");
                                }
                            }
                            txtResult.setText(result_text.toString());  */
                            Log.i("Result"," textview : "+s);
                            txtResult.setText(s);

                        }

                    }

                    @Override
                    protected void onProgressUpdate(String... values){
                        progressDialog.setMessage(values[0]);


                    }


                };

                visionTask.execute(inputStream);
            }
        });
    }
}

class VisionBatchRead implements VisionServiceClient {
    private Gson gson = new Gson();
    private final WebServiceRequest restCall;
    private final String apiRoot;
    private final String API_LINK="https://southeastasia.api.cognitive.microsoft.com/vision/v2.0";


    public VisionBatchRead(String subscriptKey, String apiRoot) {
        this.restCall = new WebServiceRequest(subscriptKey);
        this.apiRoot = apiRoot.replaceAll("/$", "");
    }


    @Override
    public AnalysisResult analyzeImage(String s, String[] strings, String[] strings1) throws VisionServiceException {
        return null;
    }

    @Override
    public AnalysisResult analyzeImage(InputStream inputStream, String[] strings, String[] strings1) throws VisionServiceException, IOException {
        return null;
    }


    public StringBuilder analyzeImage1(InputStream stream, String[] visualFeatures, String[] details) throws VisionServiceException, IOException {
        Map<String, Object> params = new HashMap<>();
        AppendParams(params, "visualFeatures", visualFeatures);
        AppendParams(params, "details", details);
        String path = apiRoot + "/read/core/asyncBatchAnalyze";
        String uri = WebServiceRequest.getUrl(path, params);

        params.clear();
        byte[] data = Utils.toByteArray(stream);
        params.put("data", data);
        Log.i("uri"," uri : "+uri);
        String json_uri = (String) this.restCall.request(uri, "POST", params, "application/octet-stream", false);
        //Log.i("json"," string json + "+json_uri);
        String json = (String) this.restCall.request(json_uri, "GET", params, "application/octet-stream", false);
        //Log.i("json"," string json + "+json);
        BatchReadResult out = this.gson.fromJson(json, BatchReadResult.class);
        while(out.status.equals("Running")){
            json = (String) this.restCall.request(json_uri, "GET", params, "application/octet-stream", false);
            //Log.i("json"," string json Loop + "+json);
            out = this.gson.fromJson(json, BatchReadResult.class);
            Log.i("out"," string json Loop + "+out.status);
        }

        /*try {
            Thread.sleep(3000);
            json = (String) this.restCall.request(json_uri, "GET", params, "application/octet-stream", false);
            Log.i("json"," string json + "+json);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        //BatchReadResult out = this.gson.fromJson(json, BatchReadResult.class);

        Log.i("out"," GO string json + "+json);

        //Log.i("out"," GO string json + "+out.recognitionResults.get(0).lines.get(0));
        StringBuilder result_text = new StringBuilder();
        try {
            JSONObject reader=new JSONObject(json);
            JSONArray sys=reader.getJSONArray("recognitionResults");
            int jsonLen=sys.length();
            for(int i=0; i<jsonLen ;i++) {
                JSONObject lines = sys.getJSONObject(i);
                Log.i("out", " GO pixel + " + lines);
                JSONArray lines1=lines.getJSONArray("lines");
                Log.i("out", " GO pixel +1 " + lines1);
                int lineLen=lines1.length();
                for(int j=0;j<lineLen;j++) {
                    JSONObject word=lines1.getJSONObject(j);
                    JSONArray word1=word.getJSONArray("words");
                    Log.i("out", " GO pixel + " + word1);
                    int wordLen=word1.length();
                    for(int k=0;k<wordLen;k++) {
                        JSONObject text=word1.getJSONObject(k);
                        String text1=text.getString("text");
                        Log.i("out", " GO pixel +text " + text1);
                        result_text.append(text1);


                    }



                }
                /*
                JSONObject words=lines.getJSONObject("words");
                int wordLen=words.length();
                for(int j=0;j<wordLen;j++) {
                    String textin=words.getString("text");
                    Log.i("out", " GO pixel + " + textin);
                }
                */

            }


            Log.i("out"," GO text + "+result_text);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        //AnalysisResult visualFeature = this.gson.fromJson(json, AnalysisResult.class);

        return result_text;
    }

    @Override
    public AnalysisInDomainResult analyzeImageInDomain(String s, Model model) throws VisionServiceException {
        return null;
    }

    @Override
    public AnalysisInDomainResult analyzeImageInDomain(String s, String s1) throws VisionServiceException {
        return null;
    }

    @Override
    public AnalysisInDomainResult analyzeImageInDomain(InputStream inputStream, Model model) throws VisionServiceException, IOException {
        return null;
    }

    @Override
    public AnalysisInDomainResult analyzeImageInDomain(InputStream inputStream, String s) throws VisionServiceException, IOException {
        return null;
    }

    @Override
    public AnalysisResult describe(String s, int i) throws VisionServiceException {
        return null;
    }

    @Override
    public AnalysisResult describe(InputStream inputStream, int i) throws VisionServiceException, IOException {
        return null;
    }

    @Override
    public ModelResult listModels() throws VisionServiceException {
        return null;
    }

    @Override
    public OCR recognizeText(String s, String s1, boolean b) throws VisionServiceException {
        return null;
    }

    @Override
    public OCR recognizeText(InputStream inputStream, String s, boolean b) throws VisionServiceException, IOException {
        return null;
    }

    @Override
    public HandwritingRecognitionOperation createHandwritingRecognitionOperationAsync(String s) throws VisionServiceException {
        return null;
    }

    @Override
    public HandwritingRecognitionOperation createHandwritingRecognitionOperationAsync(InputStream inputStream) throws VisionServiceException, IOException {
        return null;
    }

    @Override
    public HandwritingRecognitionOperationResult getHandwritingRecognitionOperationResultAsync(String s) throws VisionServiceException {
        return null;
    }

    @Override
    public byte[] getThumbnail(int i, int i1, boolean b, String s) throws VisionServiceException, IOException {
        return new byte[0];
    }

    @Override
    public byte[] getThumbnail(int i, int i1, boolean b, InputStream inputStream) throws VisionServiceException, IOException {
        return new byte[0];
    }

    private void AppendParams(Map<String, Object> params, String name, String[] args) {
        if(args != null && args.length > 0) {
            String features = StringUtils.join(args, ',');
            params.put(name, features);
        }
    }
}


class BatchReadResult {
    public String status;
}
