package com.example.aliyunmedia.yes.ok;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import android.support.v7.app.ActionBarActivity;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
@TargetApi(Build.VERSION_CODES.HONEYCOMB) public class MainActivity extends ActionBarActivity {
	private TextView tv,detail;
	private Button camerabutton,playbutton,selectvideo;
	private ProgressBar pb;
	private String path,objectname;
	private EditText filename;
	 private static final int PHOTO_REQUEST_GALLERY = 2;// �������ѡ��
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       findbyid();
    }
    private void findbyid() {
		// TODO Auto-generated method stub
    	selectvideo= (Button) findViewById(R.id.camerabutton);
    	detail= (TextView) findViewById(R.id.detail);
    	tv = (TextView) findViewById(R.id.text);
		pb = (ProgressBar) findViewById(R.id.progressBar1);
		camerabutton = (Button) findViewById(R.id.camerabutton);
		playbutton= (Button) findViewById(R.id.playbutton);
		filename=(EditText) findViewById(R.id.filename);
		
		playbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, PlayVideoActivity.class);
				intent.putExtra("objectname", objectname);
				//���û���Ŀ¼
				intent.putExtra("cache",
						Environment.getExternalStorageDirectory().getAbsolutePath()
								+ "/VideoCache/" + System.currentTimeMillis() + ".mp4");
				startActivity(intent);
			}
		});
		
		camerabutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		    beginupload();
			}
		});
	}

    public void selectvideo(View view)
    {
    	//����ͼ��
    	  Intent intent = new Intent(Intent.ACTION_PICK);
    	  //ѡ��ĸ�ʽΪ��Ƶ,ͼ���о�ֻ��ʾ��Ƶ�����ͼƬ�ϴ��Ļ����Ը�Ϊimage/*��ͼ���ֻ��ʾͼƬ��
    	           intent.setType("video/*");
    	           // ����һ�����з���ֵ��Activity��������ΪPHOTO_REQUEST_GALLERY
    	           startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }
//    /*
//          * �ж�sdcard�Ƿ񱻹���
//          */
//         private boolean hasSdcard() {
//             if (Environment.getExternalStorageState().equals(
//                     Environment.MEDIA_MOUNTED)) {
//                 return true;
//            } else {
//                 return false;
//             }
//         }
    public void beginupload(){
    	//ͨ����д�ļ����γ�objectname,ͨ���������ָ���ϴ������ص��ļ�
    	objectname=filename.getText().toString();
    	if(objectname==null||objectname.equals("")){
    		Toast.makeText(MainActivity.this, "�ļ�������Ϊ��", 2000).show();
    		return;
    	}
    	//��д�Լ���OSS��������
		String endpoint = "http://oss-cn-shanghai.aliyuncs.com";
		//��д����accessKeyId��accessKeySecret�����ܹ�����
    	OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider("5wpkJus0wsbkUefR", "Hf2xHZe0bX71h55FRAcVpIRVBQYWlO ");
		OSS oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider);
		//����3����������Ϊbucket����Object�����ϴ��ļ�·��
    	PutObjectRequest put = new PutObjectRequest("qhtmedia", objectname, path);
    	if(path==null||path.equals("")){
    		detail.setText("��ѡ����Ƶ!!!!");
    		return;
    	}
				tv.setText("�����ϴ���....");
		    	pb.setVisibility(View.VISIBLE);
    	// �첽�ϴ����������ý��Ȼص�
    	put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
    		@Override
    	    public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
    	    	Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
				}
    	});
    	@SuppressWarnings("rawtypes")
		OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
    	    @Override
    	    public void onSuccess(PutObjectRequest request, PutObjectResult result) {
    	        Log.d("PutObject", "UploadSuccess");
    	        //ȥUI�̸߳���UI
    	    	runOnUiThread(new Runnable() {
   				@Override
    				public void run() {
    					// TODO Auto-generated method stub
   				  tv.setText("UploadSuccess");
      	          pb.setVisibility(ProgressBar.INVISIBLE);
   				}
    			});
    	    }
    	    @Override
    	    public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
    	        // �����쳣
    	    	runOnUiThread(new Runnable() {
       				@Override
        				public void run() {
        					// TODO Auto-generated method stub
       					pb.setVisibility(ProgressBar.INVISIBLE);
       	    	    	tv.setText("Uploadfile,localerror");
       				}
        			});
    	        if (clientExcepion != null) {
    	            // �����쳣�������쳣��
    	            clientExcepion.printStackTrace();
    	        }
    	        if (serviceException != null) {
    	            // �����쳣
    	        	tv.setText("Uploadfile,servererror");
    	            Log.e("ErrorCode", serviceException.getErrorCode());
    	            Log.e("RequestId", serviceException.getRequestId());
    	            Log.e("HostId", serviceException.getHostId());
    	            Log.e("RawMessage", serviceException.getRawMessage());
    	        }
    	    }
    	});
    	// task.cancel(); // ����ȡ������
//    	 task.waitUntilFinished(); // ���Եȴ�ֱ���������
}
         @Override
              protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                 if (requestCode == PHOTO_REQUEST_GALLERY) {
                     // ����᷵�ص�����
                     if (data != null) {
                         // �õ���Ƶ��ȫ·��
                        Uri uri = data.getData();
                        getRealFilePath(MainActivity.this,uri);
                     }
                 } 
                 super.onActivityResult(requestCode, resultCode, data);
             }
        /* ������4.4��ͨ��Uri��ȡ·���Լ��ļ���һ�ַ���������õ���·�� /storage/emulated/0/video/20160422.3gp��
                                 ͨ���������һ��/�Ϳ�����String�н�ȡ��*/
         public  void getRealFilePath( final Context context, final Uri uri ) {
             if ( null == uri ) return ;
             final String scheme = uri.getScheme();
             String data = null;
             if ( scheme == null )
                 data = uri.getPath();
             else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
                 data = uri.getPath();
             } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
                 Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
                 if ( null != cursor ) {
                     if ( cursor.moveToFirst() ) {
                         int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                         if ( index > -1 ) {
                             data = cursor.getString( index );
                         }
                     }
                     cursor.close();
                 }
             }
             path=data;
             String b = path.substring(path.lastIndexOf("/") + 1, path.length());
             detail.setText(b);
         }
}
