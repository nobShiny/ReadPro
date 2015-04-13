package com.shiny.douban;

import com.shiny.douban.entity.Subject;
import com.shiny.douban.util.ConvertUtil;
import com.shiny.douban.util.StatusUtil;
import com.google.gdata.data.douban.CollectionEntry;
import com.google.gdata.data.douban.SubjectEntry;
import com.google.gdata.data.extensions.Rating;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * �ղ�����
 * @author Administrator
 *
 */
public class ShortReviewEditActivity extends BaseActivity {

	private final int SUCCESS = 1;
	private TextView txtCollectionTip;
	private Subject subject;
	private EditText edtReviewTags;
	private EditText edtReviewContent;
	private ProgressDialog dialog;
	private RatingBar reviewRatingbar;
	private Integer buttonId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.short_review_edit);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar3);
		txtCollectionTip = (TextView) findViewById(R.id.txtCollectionTip);

		Bundle extras = getIntent().getExtras();
		subject = extras != null ? (Subject) extras.getSerializable("subject")
				: null;
		buttonId = extras != null ? (Integer) extras.getSerializable("id")
				: null;

		edtReviewTags = (EditText) findViewById(R.id.EdtReviewTags);
		edtReviewContent = (EditText) findViewById(R.id.EdtReviewContent);
		reviewRatingbar = (RatingBar) findViewById(R.id.ratingbar);

		// ���ñ���
		String title = StatusUtil.getStatusDesc(buttonId) + "��"
				+ this.subject.getTitle() + "��";
		txtCollectionTip.setText(title);

		// ���ð�ť�¼�
		initView();

		// ��ȡ����
		setData();
	}

	private void setData() {
		if (this.subject != null) {
			edtReviewTags.setText(subject.getMyTags());
			edtReviewContent.setText(subject.getMyShortComment());
			reviewRatingbar.setRating(subject.getMyRating());
		}
	}

	private void initView() {
		dialog = new ProgressDialog(this);

		Button btnSave = (Button) findViewById(R.id.btnSave);
		// ����
		btnSave.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				doPost();
			}

		});

		// ȡ��
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				goBack();
			}

		});

		// ����
		ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
		backButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				goBack();
			}

		});

	}

	protected void doPost() {
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					dialog.hide();
					String message = "�ղسɹ���";
					Toast.makeText(ShortReviewEditActivity.this, message,
							Toast.LENGTH_SHORT).show();
					Intent it = new Intent();
					it.putExtra("status", StatusUtil.getStatus(buttonId));
					it.putExtra("statusDesc", StatusUtil.getStatusDesc(buttonId));
					it.putExtra("rating", reviewRatingbar.getRating());
					it.putExtra("tags", edtReviewTags.getText().toString().trim());
					setResult(SUCCESS, it);
					finish();
				} else {
					dialog.hide();
					String message = "�ղ�ʧ�ܣ�";
					Toast.makeText(ShortReviewEditActivity.this, message,
							Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			protected void onPreExecute() {
				dialog.setMessage("�����ύ����...");
				dialog.show();
			}

			@Override
			protected Boolean doInBackground(Void... arg0) {
				String tags = edtReviewTags.getText().toString().trim();
				String content = edtReviewContent.getText().toString().trim();
				int ratingValue = (int) reviewRatingbar.getRating();
				Rating rating = new Rating();
				rating.setValue(ratingValue);
				com.google.gdata.data.douban.Status status = new com.google.gdata.data.douban.Status();
				status.setContent(StatusUtil.getStatus(buttonId));
				SubjectEntry subjectEntry = new SubjectEntry();
				subjectEntry.setId(subject.getId());
				// ����
				if (subject.getStatus() != null) {
					try {
						CollectionEntry ce = new CollectionEntry();
						ce.setId(subject.getCollectionUrl());
						com.google.gdata.data.douban.Subject subjectObject = new com.google.gdata.data.douban.Subject();
						subjectObject.setId(subject.getId());
						ce.setSubjectEntry(subjectObject);
						getDoubanService().updateCollection(ce, status,
								ConvertUtil.convertTags(tags), rating);
						return true;
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
				// ����
				else {
					try {
						getDoubanService().createCollection(status,
								subjectEntry, ConvertUtil.convertTags(tags),
								rating);
						return true;
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
			}

		}.execute();

	}

	protected void goBack() {
		String tags = this.edtReviewTags.getText().toString().trim();
		String content = this.edtReviewContent.getText().toString().trim();
		if (("".equals(tags)) && ("".equals(content))) {
			finish();
			return;
		}

		if (this.subject != null) {
			String mytags = subject.getMyTags().trim();
			if (mytags.equals(tags)) {
				finish();
				return;
			}
		}

		new AlertDialog.Builder(ShortReviewEditActivity.this)
				.setTitle("��ʾ")
				.setMessage("����δ���棬ȷ��Ҫ������")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						finish();
					}
				})
				.setNeutralButton("ȡ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
					}

				}).show();
	}

	protected void onPause() {
		super.onPause();
		if (this.dialog != null)
			this.dialog.dismiss();
	}

}
