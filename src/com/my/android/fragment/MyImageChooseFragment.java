package com.my.android.fragment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import com.my.android.utils.ImageUtil;
import com.my.android.utils.LogUtil;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

@SuppressLint("SdCardPath")
/**
 * 选择图片选项卡
 * @author hushuai
 */
//如果Activity中也重载了onActiivtyResult方法，一定调用super.onActivityResult，否则处理不了图片
public class MyImageChooseFragment extends MyActionSheetFragment implements MyActionSheetFragment.ActionSheetListener{
	private static final int CAMERA_REQUEST_CODE = 10;
	private static final int ALBUM_REQUEST_CODE = 11;
	private static final String[] mitems = {"拍照","从相册中选择"};
	
	private static final String IMAGE_NAME = "temp.jpeg";
	private boolean isCrop = false; //是否需要裁剪
	private int scale_width = 1; //需要裁剪时， 裁剪的宽高比例 ，1 ：1 即为正方形
	private int scale_height = 1;
	private ChooseCompleteListener completeListener;
	
	public static MyImageChooseFragment newInstance(ChooseCompleteListener listener) {
		return MyImageChooseFragment.newInstance(false, 0, 0,listener);
	}
	
	/**
	 * @param isCrop 是否需要裁剪
	 * @param scale_width、scale_height 裁剪比例 1：1表示裁剪正方形
	 * <p>isCrop为true时scale_width、scale_height参数才有效
	 */
	public static MyImageChooseFragment newInstance(boolean isCrop, int scale_width, int scale_height,ChooseCompleteListener listener) {
		MyImageChooseFragment msf = new MyImageChooseFragment();
		msf.isCrop = isCrop;
		msf.completeListener = listener;
		if(isCrop){
			if(scale_width != 0 && scale_height != 0){
				msf.scale_width = scale_width;
				msf.scale_height = scale_height;
			}
		}
		msf.setItems(mitems);
		msf.setActionSheetListener(msf);
		return msf;
	}

	@Override
	public void onItemClick(MyActionSheetFragment actionSheet, int position) {
		if(position == 0){
			chooseFromCamera();//拍照
		}else{
			chooseFromAlbum();//相册
		}
	}
	
	/**拍照选择图片*/
	private void chooseFromCamera() {
		File file = getActivity().getExternalCacheDir();
		if(!file.exists()){
			file.mkdir();
		}
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//action is capture
		File save = new File(file,IMAGE_NAME);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(save));
		startActivityForResult(intent, CAMERA_REQUEST_CODE);
	}
	
	/**从相册中选择图片*/
	private void chooseFromAlbum() {
		File file = getActivity().getExternalCacheDir();
		if(!file.exists()){
			file.mkdir();
		}
		File save = new File(file,IMAGE_NAME);
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		if(isCrop){
			intent.putExtra("crop", "circle");
			intent.putExtra("aspectX", scale_width);
			intent.putExtra("aspectY", scale_height);
		}
		intent.putExtra("scale", true);
		intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(save));
		startActivityForResult(intent, ALBUM_REQUEST_CODE);
	}

	@Override
	public void onCancel(MyActionSheetFragment actionSheet) {
		//取消时要播放动画，把隐藏的view先显示
		getView().setVisibility(View.VISIBLE);
		super.dismissWithAnim();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Uri uri = null;
		Bitmap bitmap = null;
		switch (requestCode) {
		case ALBUM_REQUEST_CODE:
			if(data == null){
				break;
			}
			uri = data.getData();
			if(uri != null){
				String path = ImageUtil.getImagePathFromUri(getActivity(), uri);
				//相册图片有的手机有旋转，获取旋转的角度
				int angle = ImageUtil.getImageAngle(path);
				bitmap = ImageUtil.compressBitmap(path, 480, 800);
				//把图片角度旋转正确
				bitmap = ImageUtil.rotateImageView(angle, bitmap);
			}
			if(bitmap == null){
				bitmap = data.getParcelableExtra("data");
			}
			break;
		case CAMERA_REQUEST_CODE:
			File file = new File(getActivity().getExternalCacheDir(),IMAGE_NAME);
			if(!file.exists()){
				break;
			}
			String path = file.getPath();
			int angle = ImageUtil.getImageAngle(path);
			bitmap = ImageUtil.compressBitmap(path, 480, 800);//压缩图片
			if(angle != 0){//拍照后有的手机图片有旋转，这里处理下
				try {
					bitmap = ImageUtil.rotateImageView(angle, bitmap);
					FileOutputStream fos = new FileOutputStream(file);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
					fos.close();
				} catch (IOException e) {
					LogUtil.log(e);
				}
			}
			
			if(isCrop){
				uri = Uri.fromFile(file);
				cropImage(uri,ALBUM_REQUEST_CODE);
				return;
			}
			break;
		default:
			break;
		}
		//图片处理完成，fragment dismiss掉。
		super.dismiss();
		File file = null;
		if(bitmap != null){
			try {
				//相册选择可能不输出图片，所以再把bitmap存一遍，确保文件存在
				file = new File(getActivity().getExternalCacheDir(),IMAGE_NAME);
				FileOutputStream fos = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				fos.close();
			} catch (Exception e) {
				LogUtil.log(e);
			}
		}
		if(completeListener != null){
			completeListener.chooseComplete(bitmap,file==null?null:file.getPath());
		}
	}
	
	@Override
	public void dismissWithAnim() {
		//要接收处理选择的图片，所以这里不能直接dismiss掉,处理完图片后再dismiss，否则选择图片完成后接收不了图片信息, 
		getView().setVisibility(View.GONE);
	}
	
	/**图片选择完成的监听回调*/
	public interface ChooseCompleteListener{
		/**
		 * @param bitmap  返回的位图
		 * @path 图片的路径
		 */
		public void chooseComplete(Bitmap bitmap,String path);
	}
	
	//截取图片
	private void cropImage(Uri uri, int requestCode){
		Intent intent = new Intent("com.android.camera.action.CROP");  
        intent.setDataAndType(uri, "image/*");  
        intent.putExtra("crop", "true");  
        intent.putExtra("aspectX", scale_width);  
        intent.putExtra("aspectY", scale_height);  
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);  
	    startActivityForResult(intent, requestCode);
	}
}
