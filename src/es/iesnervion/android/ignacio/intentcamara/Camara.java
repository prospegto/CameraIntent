package es.iesnervion.android.ignacio.intentcamara;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

public class Camara extends Activity {

	private static final int REQUEST_CODE_IMAGEN = 1;
	private static final int REQUEST_CODE_VIDEO = 2;
	private static final int REQUEST_CODE_GALERIA = 3;
	public static final int TYPE_IMAGEN = 10;
	public static final int TYPE_VIDEO = 20;

	private static final String DIRECTORIO = "DirectorioImagenIntent";

	private Uri uri;

	private ImageView imagen;
	private VideoView video;
	private Button btnCapImagen, btnGrabVideo, btnGaleria;

	Bitmap bitmap;


	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camara);

		imagen = (ImageView) findViewById(R.id.imagen);
		video = (VideoView) findViewById(R.id.video);
		btnCapImagen = (Button) findViewById(R.id.btnImagen);
		btnGaleria = (Button) findViewById(R.id.btnGaleria);
		btnGrabVideo = (Button) findViewById(R.id.btnVideo);
		btnCapImagen.setOnClickListener(new View.OnClickListener() {		 
			@Override
			public void onClick(View v) {
				hacerFoto();
			}
		});
		btnGrabVideo.setOnClickListener(new View.OnClickListener() {		 
			@Override
			public void onClick(View v) {
				grabarVideo();
			}
		});

		btnGaleria.setOnClickListener(new View.OnClickListener() {		 
			@Override
			public void onClick(View v) {
				abrirGaleria();
			}


		});

		video.setOnPreparedListener (new OnPreparedListener() {                    
			@Override
			public void onPrepared(MediaPlayer mp) {
				mp.setLooping(true);
			}
		});


		bitmap = (Bitmap) getLastNonConfigurationInstance();
		if(bitmap!=null){
			imagen.setImageBitmap(bitmap);
		}			

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camara, menu);
		return true;
	}


	private void abrirGaleria() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);	
		intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		startActivityForResult(intent, REQUEST_CODE_GALERIA);
	}

	private void hacerFoto(){
		Intent intentFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);	 
		uri = obtenerDirectorioPorUri(TYPE_IMAGEN);	 
		intentFoto.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intentFoto, REQUEST_CODE_IMAGEN);
	}

	private void grabarVideo(){
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE); 
		uri = obtenerDirectorioPorUri(TYPE_VIDEO);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); //1 alta | 0 baja
		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, REQUEST_CODE_VIDEO);
	}

	private Uri obtenerDirectorioPorUri(int typeImagen) {
		// TODO Auto-generated method stub
		return Uri.fromFile(crearArchivo(typeImagen));
	}

	private File crearArchivo(int typeImagen) {
		// TODO Auto-generated method stub
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),DIRECTORIO);
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.e("CREANDO DIRECTORIO", "Fallo al crear "+ DIRECTORIO);
				return null;
			}
		}

		File archivo;
		String diferenciador = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(new Date());

		if (typeImagen == TYPE_IMAGEN) {
			archivo = new File(mediaStorageDir.getPath() + File.separator+ "imgDesdeIntent"+diferenciador+".jpg");
		} else if (typeImagen == TYPE_VIDEO) {
			archivo = new File(mediaStorageDir.getPath() + File.separator+ "videoDesdeIntent"+diferenciador+".mp4");
		} else {
			return null;
		}

		return archivo;
	}

	protected void onActivityResult(int requestCode, int resultCode,Intent data) {
		if (requestCode == REQUEST_CODE_IMAGEN) {
			if (resultCode == RESULT_OK) {
				mostrarImagen();
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(getApplicationContext(),"Cancelada imagen", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_SHORT).show();
				Log.e("CAPTURANDO IMAGEN", "Fallo al capturar imagen");
			}
		} else if (requestCode == REQUEST_CODE_VIDEO) {
			if (resultCode == RESULT_OK) {
				mostrarVideo();
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(getApplicationContext(),"Cancelado vídeo", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_SHORT).show();
				Log.e("CAPTURANDO VIDEO", "Fallo al capturar video");
			}
		}
		else if (requestCode == REQUEST_CODE_GALERIA) {
    		if (resultCode == RESULT_OK) {
    			Uri selectedImage = data.getData();
    			InputStream is;
    			try {
    				is = getContentResolver().openInputStream(selectedImage);
    		    	BufferedInputStream bis = new BufferedInputStream(is);
    		    	Bitmap bitmap = BitmapFactory.decodeStream(bis);            
    		    	ImageView iv = (ImageView)findViewById(R.id.imagen);
    		    	iv.setImageBitmap(bitmap);			
    		    	mostrarImagen();
    			} catch (FileNotFoundException e) {}
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(getApplicationContext(),"Cancelada galería", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_SHORT).show();
				Log.e("CAPTURANDO IMG GALERÍA", "Fallo al seleccionar imagen de galeria");
			}
		}
	}




	private void mostrarVideo() {
		// TODO Auto-generated method stub
		try {
			imagen.setVisibility(View.GONE);
			video.setVisibility(View.VISIBLE);
			video.setVideoPath(uri.getPath());
			video.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void mostrarImagen() {
		// TODO Auto-generated method stub
		try {
			video.setVisibility(View.GONE);
			imagen.setVisibility(View.VISIBLE);
			BitmapFactory.Options opcionesGraficas = new BitmapFactory.Options();
			opcionesGraficas.inSampleSize = 8;
			bitmap = BitmapFactory.decodeFile(uri.getPath(),opcionesGraficas);
			imagen.setImageBitmap(bitmap);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return bitmap;
	}




}
