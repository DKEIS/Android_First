package com.example.hyojong.dkeis;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hyojong.dkeis.Adapter.StyleActivity;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import java.util.concurrent.Future;

public class TransferFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private TextView content;
    private ImageView selectImage;
    private ImageView styleimage;
    private ImageView changeImage;
    private String path, userURL, styleURL;



    private String absoultePath;

    Uri photoUri;
    private String currentPhotoPath;//실제 사진 파일 경로
    String mImageCaptureName;//이미지 이름


    public TransferFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TransferFragment newInstance() {
        TransferFragment fragment = new TransferFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transfer, container, false);

        content = (TextView) view.findViewById(R.id.content);
        content.setText("바꾸고 싶은 이미지를 집어넣어라!!");


        selectImage = (ImageView)view.findViewById(R.id.cameraimage);
        styleimage = (ImageView)view.findViewById(R.id.paintimage);
        changeImage = (ImageView)view.findViewById(R.id.albumimage);

        // 자신의 사진 선택
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeAlbumAction();
            }
        });

        //  Style 선택
        styleimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), StyleActivity.class);
                startActivityForResult(intent,3);
            }
        });

        // 변환
        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Node.js or JSP 파일 업로드[][][][][][][][][][][][][]][][][][][][][]
                // 1) User Image 웹 서버에 업로드, 2) Style URL 전송, 3) Node js 호출?
                //path = getPathFromURI(selectImage.set);
                path = absoultePath;
                //System.out.println("###############PATH: " + path);
                File f = new File(path);
                Future uploading = Ion.with(getActivity())
                        //.load("http://192.168.150.1:8080/upload")
                        .load("http://114.70.235.44:8888/upload")
                        .setMultipartFile("image", f)
                        .setMultipartParameter("style", styleURL)
                        .asString()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<String>>() {
                            @Override
                            public void onCompleted(Exception e, Response<String> result) {
                                try {

                                    userURL = "114.70.235.44:18081/capstone/upload/" + absoultePath.split("/")[absoultePath.split("/").length-1];
                                    //System.out.println("@@@@@@@@@@@@@@@@@@@" + result.getResult());
                                    String msg = result.getResult();
                                    //JSONObject jobj = new JSONObject(result.getResult());
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }

                            }
                        });

            }
        });

        return view;
    }

    private String getPathFromURI(Uri uri) {
        String filePath = "";
        filePath = uri.getPath();

        if (filePath.startsWith("/storage"))
            return filePath;

        String wholeID = DocumentsContract.getDocumentId(uri);

        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Files.FileColumns.DATA };

        //   파일의 이름을 통해 where 조건식을 만듭니다.

        String sel = MediaStore.Files.FileColumns.DATA + " LIKE '%" + id + "%'";

        //    External storage에 있는 파일의 DB를 접근하는 방법 입니다.
        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Files.getContentUri("external"),
                column, sel, null, null);

        //    SQL문으로 표현하면 아래와 같이 되겠죠????
        //     SELECT _dtat FROM files WHERE _data LIKE '%selected file name%'

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void takeAlbumAction() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode ,int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                photoUri = data.getData();
                //styleURL = photoUri.getPath().toString();
                Log.d("Get Photo", photoUri.getPath().toString());
            case 0:

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(photoUri, "image/*");

                intent.putExtra("outputX", 200);
                intent.putExtra("outputY",200);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data",true);
                startActivityForResult(intent, 2);

                break;

            case 2:
                final Bundle extras = data.getExtras();
                String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DKEIS/" + System.currentTimeMillis() +".jpg";

                if(extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    selectImage.setImageBitmap(photo);
                    storeCropImage(photo, filepath);
                    absoultePath = filepath;
                    //System.out.println("#########FILEPATH:" + absoultePath);

                }
                break;
            case 3:
                Intent intents = data;
                String url = intents.getStringExtra("url");
                styleURL = url;
                Glide.with(getContext()).load(url).into(styleimage);
                break;

            default:
                System.out.println("!!!!!1취소");
                break;
        }
    }

    private void storeCropImage(Bitmap bitmap, String filePath){
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DKEIS";
        File directory_DKEIS = new File(dirPath);
        if(!directory_DKEIS.exists())
            directory_DKEIS.mkdir();

        File copyFile = new File(filePath);
        BufferedOutputStream out = null;

        try {
            copyFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

           // sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
