package com.shiny.douban;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.douban.NoteEntry;

public class NoteEditActivity extends BaseActivity {

	private EditText edtTitle; // ����
	private EditText edtContent; // ����
	private String id;
	private String title;
	private String content;
	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.note_edit);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar);
		initView();
		setData();

	}

	// ��ʼ���ؼ�
	private void initView() {

		dialog = new ProgressDialog(this);
		edtTitle = (EditText) findViewById(R.id.EditTextTitle);
		edtContent = (EditText) findViewById(R.id.EditTextContent);

		if (id == null) {
			// ����Ƶ�������
			edtContent.setGravity(Gravity.TOP);
		} else {
			// ����Ƶ�������
			edtContent.setGravity(Gravity.BOTTOM);
		}

		// ���ذ�ť
		ImageButton editButton = (ImageButton) findViewById(R.id.edit_button);
		editButton.setVisibility(View.INVISIBLE);

		// ���˰�ť
		ImageButton backButton = (ImageButton) findViewById(R.id.back_button);
		backButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				goBack();
			}

		});

		// ȡ����ť
		Button cancelButton = (Button) findViewById(R.id.btnCancel);
		cancelButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				goBack();
			}

		});

		// ���水ť
		Button saveButton = (Button) findViewById(R.id.btnSave);
		saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doPost();
			}
		});
	}

	private void doPost() {
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					dialog.hide();
					Intent it = new Intent();
					setResult(1, it);
					finish();
				} else {
					String message = "";
					if (id == null) {
						message = "�ռ�����ʧ�ܣ�";
					} else {
						message = "�ռ��޸�ʧ�ܣ�";
					}
					Toast.makeText(NoteEditActivity.this, message,
							Toast.LENGTH_SHORT);
				}
			}

			@Override
			protected void onPreExecute() {
				dialog.setMessage("�����ύ����...");
				dialog.show();
			}

			@Override
			protected Boolean doInBackground(Void... arg0) {
				String title = edtTitle.getText().toString().trim();
				String content = edtContent.getText().toString().trim();
				// ����
				if (id != null) {
					NoteEntry ne = new NoteEntry();
					ne.setId(id);
					try {
						getDoubanService().updateNote(ne,
								new PlainTextConstruct(title),
								new PlainTextConstruct(content), "private",
								"no");
						return true;
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
				// ����
				else {
					try {
						getDoubanService().createNote(
								new PlainTextConstruct(title),
								new PlainTextConstruct(content), "private",
								"no");
						return true;
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
			}

		}.execute();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	// ��������
	private void setData() {

		Bundle extras = getIntent().getExtras();
		id = extras != null ? extras.getString("id") : null;
		title = extras != null ? extras.getString("title") : null;
		content = extras != null ? extras.getString("content") : null;
		if (title != null) {
			edtTitle.setText(title);
		}
		if (content != null) {
			edtContent.setText(content);
		}

	}

	// ����
	private void goBack() {
		String title = edtTitle.getText().toString().trim();
		String content = edtContent.getText().toString().trim();

		// û�����ݣ�ֱ�ӻ���
		if ("".equals(title) && "".equals(content)) {
			finish();
			return;
		}

		// δ���޸ģ�ֱ�ӻ���
		if (title.equals(this.title) && content.equals(this.content)) {
			finish();
			return;
		}

		new AlertDialog.Builder(NoteEditActivity.this).setTitle("��ʾ")
				.setMessage("����δ���棬ȷ��Ҫ������").setPositiveButton("ȷ��",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								finish();
							}
						}).setNeutralButton("ȡ��",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
							}

						}).show();
	}

}
