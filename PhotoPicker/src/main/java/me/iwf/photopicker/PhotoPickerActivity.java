package me.iwf.photopicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.entity.Photo;
import me.iwf.photopicker.event.OnItemCheckListener;
import me.iwf.photopicker.fragment.ImagePagerFragment;
import me.iwf.photopicker.fragment.PhotoPickerFragment;

import static android.widget.Toast.LENGTH_LONG;

public class PhotoPickerActivity extends AppCompatActivity implements View.OnClickListener, OnItemCheckListener {

    private PhotoPickerFragment pickerFragment;
    private ImagePagerFragment imagePagerFragment;

    public final static String EXTRA_MAX_COUNT = "MAX_COUNT";
    public final static String EXTRA_SHOW_CAMERA = "SHOW_CAMERA";
    public final static String EXTRA_SHOW_GIF = "SHOW_GIF";
    public final static String KEY_SELECTED_PHOTOS = "SELECTED_PHOTOS";
    public final static String EXTRA_INTENT_FROM= "intent_from";



    public final static int DEFAULT_MAX_COUNT = 9; //-1则不限制

    private int maxCount = DEFAULT_MAX_COUNT;

    private boolean showGif = false;
    private TextView toolbarMenu;
    private String from;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean showCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        boolean showGif = getIntent().getBooleanExtra(EXTRA_SHOW_GIF, false);
        from = getIntent().getStringExtra(EXTRA_INTENT_FROM);
        setShowGif(showGif);

        setContentView(R.layout.activity_photo_picker);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(null);
        mToolbar.setNavigationIcon(R.drawable.photo_arrow_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        TextView toolbarTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("图片");
        toolbarMenu = (TextView) mToolbar.findViewById(R.id.toolbar_menu);
        if(!TextUtils.isEmpty(from)&&("PublishAddWordActivity".equals(from)
                ||"PublishNewActivity".equals(from)
                || "SetHeadIconFragment".equals(from)
                || "PersonalHomePageActivity".equals(from)
        )){
          toolbarMenu.setText("确定");

        }else {
            toolbarMenu.setText("发送");
        }
        toolbarMenu.setTextColor(0xff999999);
        toolbarMenu.setEnabled(false);
        toolbarMenu.setOnClickListener(this);

        maxCount = getIntent().getIntExtra(EXTRA_MAX_COUNT, DEFAULT_MAX_COUNT);

        pickerFragment = (PhotoPickerFragment) getSupportFragmentManager().findFragmentById(R.id.photoPickerFragment);

        pickerFragment.getPhotoGridAdapter().setShowCamera(showCamera);

        pickerFragment.getPhotoGridAdapter().setOnItemCheckListener(this);

    }


    /**
     * Overriding this method allows us to run our exit animation first, then exiting
     * the activity when it complete.
     */
    @Override
    public void onBackPressed() {
        if (imagePagerFragment != null && imagePagerFragment.isVisible()) {
            imagePagerFragment.runExitAnimation(new Runnable() {
                public void run() {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack();
                    }
                }
            });
        } else {
            super.onBackPressed();
        }
    }


    public void addImagePagerFragment(ImagePagerFragment imagePagerFragment) {
        this.imagePagerFragment = imagePagerFragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, this.imagePagerFragment)
                .addToBackStack(null)
                .commit();
    }



    public PhotoPickerActivity getActivity() {
        return this;
    }

    public boolean isShowGif() {
        return showGif;
    }

    public void setShowGif(boolean showGif) {
        this.showGif = showGif;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.toolbar_menu){
            Intent intent = new Intent();
            ArrayList<String> selectedPhotos = pickerFragment.getPhotoGridAdapter().getSelectedPhotoPaths();
            intent.putStringArrayListExtra(KEY_SELECTED_PHOTOS, selectedPhotos);
            intent.putExtra("type","choice_photo");
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public boolean OnItemCheck(int position, Photo path, boolean isCheck, int selectedItemCount) {
        int total = selectedItemCount + (isCheck ? -1 : 1);

        toolbarMenu.setEnabled(total > 0);
        if (total > 0){
            toolbarMenu.setTextColor(0xffffffff);
        }

        if (maxCount <= 1&&maxCount!=-1) {
            List<Photo> photos = pickerFragment.getPhotoGridAdapter().getSelectedPhotos();
            if (!photos.contains(path)) {
                photos.clear();
                pickerFragment.getPhotoGridAdapter().notifyDataSetChanged();
            }
            return true;
        }

        if (total > maxCount&&maxCount!=-1) {
            Toast.makeText(getActivity(), getString(R.string.over_max_count_tips, maxCount),
                    LENGTH_LONG).show();
            return false;
        }
        if(!TextUtils.isEmpty(from)&&("PublishAddWordActivity".equals(from)||"PublishNewActivity".equals(from))){
            if(maxCount==-1){
                toolbarMenu.setText(getString(R.string.sure_with_count_no_max,total));

            }else {
                toolbarMenu.setText(getString(R.string.sure_with_count, total, maxCount));
            }

        }else {
            toolbarMenu.setText(getString(R.string.done_with_count, total, maxCount));

        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
