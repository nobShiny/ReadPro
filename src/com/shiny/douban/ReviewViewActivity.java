package com.shiny.douban;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.shiny.douban.entity.Review;
import com.shiny.douban.util.NetUtil;

/**
 * �鿴����
 * 
 * @author chenyc
 * 
 */
public class ReviewViewActivity extends BaseActivity {

	private TextView txtReviewTitle;
	private TextView txtReviewContent;
	private TextView txtReviewComment;
	private ImageView userImageView;
	private TextView txtSubjectTitle;
	private TextView txtUserInfo;
	private ProgressDialog dialog;
	private RatingBar ratingBar;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.review_view);

		txtReviewTitle = (TextView) findViewById(R.id.review_title);
		txtReviewContent = (TextView) findViewById(R.id.review_content);
		txtReviewComment = (TextView) findViewById(R.id.review_comments);
		userImageView = (ImageView) findViewById(R.id.user_img);
		txtSubjectTitle = (TextView) findViewById(R.id.subject_title);
		txtUserInfo = (TextView) findViewById(R.id.user_info);
		ratingBar = (RatingBar) findViewById(R.id.ratingbar);
		ratingBar.setVisibility(View.INVISIBLE);
		dialog = new ProgressDialog(this);

		// ���˰�ť
		ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
		backButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}

		});

		Bundle extras = getIntent().getExtras();
		Review review = extras != null ? (Review) extras
				.getSerializable("review") : null;
		if (review != null) {
			TextView titleView = (TextView) findViewById(R.id.myTitle);
			titleView.setText("��" + review.getSubject().getTitle() + "��������");
			fillData(review);
		}
	}

	// ��ȡ��ϸ����
	private void fillData(Review review) {
		new AsyncTask<Review, Void, Review>() {

			@Override
			protected Review doInBackground(Review... args) {
				Review review = args[0];
				try {
					review = NetUtil.getReviewContentAndComments(review);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return review;
			}

			@Override
			protected void onPostExecute(Review review) {
				dialog.hide();
				super.onPostExecute(review);
				txtReviewTitle.setText(review.getTitle());
				txtReviewContent.setText(Html.fromHtml(review.getContent()));
				txtReviewComment.setText(Html.fromHtml(review.getComments()));
				if (review.getAuthorImage() != null) {
					userImageView.setImageBitmap(review.getAuthorImage());
				}
				txtUserInfo.setText(" �����ˣ�" + review.getAuthorName());
				txtSubjectTitle.setText("��" + review.getSubject().getTitle()
						+ "��������");
				ratingBar.setRating(review.getRating());
				ratingBar.setVisibility(View.VISIBLE);
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog.setMessage("���ڼ�������...");
				dialog.show();
			}

		}.execute(review);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (dialog != null) {
			dialog.dismiss();
		}
	}

}
