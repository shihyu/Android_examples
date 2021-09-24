// TestActivity.java

public class TestActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onResume() {
        super.onResume();

        int type = 0;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(mContext);

        if (account != null && account.getGrantedScopes().contains(new Scope(DriveScopes.DRIVE_FILE))) {
            type = 1;
        }

        updateUI(type);
    }

    private void updateUI(int type) {
        if (type == 0) {
            // 구글 드라이브 연동 버튼 보이기.
        } else {

            // 파일리스트 액티비티로 이동 버튼 보이기.
            // 로그아웃 버튼 보이기.
        }
    }

    // 구글 드라이브 연동 버튼 눌렸을 때.
    private void loginGoogleDriveBtnPressed(View v) {
        GoogleSignInOptions signInOptions =
            new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
        .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, signInOptions);

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    // 파일 보는 화면으로 이동하는 버튼 눌렸을 때.
    private void goFileListActivity(View v) {
        Intent intent = new Intent(TestActivity.this, ThirdPartyFileListActivity.class);
        startActivity(intent);
    }

    private void logout(final int type) {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateUI(0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                handleSignInResult(data);
            }
        }
    }

    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
        .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
            @Override
            public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                Log.d(TAG, "Signed in as " + googleSignInAccount.getEmail());

                updateUI(1);

                // Use the authenticated account to sign in to the Drive service.
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Unable to open file from picker.", e);
            }
        });
    }
}

// ThirdPartyFileListActivity.java
// 여기서 File은 com.google.api.services.drive.model.File이다.

public class ThirdPartyFileListActivity extends AppCompatActivity {
    // ...
    private String mPath;
    private DriveServiceHelper mDriveServiceHelper;
    private ProgressDialog mDialog;
    private CancellationTokenSource cts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thirdparty_filelist);

        mDialog = new ProgressDialog(this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setCancelable(true);

        mPath = getIntent().getStringExtra("path");

        // 구글은 최상위 폴더는 root이다.
        if (mPath == null) {
            mPath = "root";
        }

        mFilesAdapter = new FileListAdapter();
        ListView fileLists = findViewById(R.id.fileLists);
        fileLists.setAdapter(mFilesAdapter);

        fileLists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File item = (File)mFilesAdapter.getItem(position);

                // 폴더면 다음 액티비티로 넘기고 파일이면 다운받는다.
                if ("application/vnd.google-apps.folder".equals(item.getMimeType())) {
                    Intent intent = new Intent(ThirdPartyFileListActivity.this, ThirdPartyFileListActivity.class);
                    intent.putExtra("email", account);
                    intent.putExtra("path", item.getId());
                    startActivity(intent);

                }  else if ("application/vnd.google-apps.document".equals(item.getMimeType()) ||
                            "application/vnd.google-apps.presentation".equals(item.getMimeType()) ||
                            "application/vnd.google-apps.spreadsheet".equals(item.getMimeType())) {

                    mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            mDialog.dismiss();
                            cts.cancel();
                        }
                    });
                    mDialog.setMessage("Downloading");
                    mDialog.show();
                    cts = new CancellationTokenSource();
                    mDriveServiceHelper.readDocsFile(item.getId(), cts.getToken())
                    .addOnSuccessListener(new OnSuccessListener<Pair<String, byte[]>>() {
                        @Override
                        public void onSuccess(Pair<String, byte[]> stringPair) {
                            mDialog.dismiss();
                            String path = third_party_dir + stringPair.first;
                            File file = new File(path);

                            if (writeBytesToFile(file, stringPair.second)) {
                                viewFileInExternalApp(file);
                            } else {

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    })
                    .addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                            Log.d(TAG, "canceld docs");
                        }
                    });
                } else {
                    cts = new CancellationTokenSource();
                    mDriveServiceHelper.readFile(item.getId(), cts.getToken())
                    .addOnSuccessListener(new OnSuccessListener<Pair<String, byte[]>>() {
                        @Override
                        public void onSuccess(Pair<String, byte[]> stringPair) {

                            String path = third_party_dir + stringPair.first;
                            Log.d(TAG, "path : " + path);
                            File file = new File(path);

                            if (writeBytesToFile(file, stringPair.second)) {
                                viewFileInExternalApp(file);
                            } else {

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mDriveServiceHelper == null) {
            GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
            GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(
                    this, Collections.singleton(DriveScopes.DRIVE_FILE));
            credential.setSelectedAccount(googleSignInAccount.getAccount());
            Drive googleDriveService =
                new Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                credential)
            .setApplicationName("Drive API Migration")
            .build();

            // The DriveServiceHelper encapsulates all REST API and SAF functionality.
            // Its instantiation is required before handling any onClick actions.
            mDriveServiceHelper = new DriveServiceHelper(googleDriveService);
            account = googleSignInAccount.getEmail();
        }

        loadData();
    }

    private void loadData() {

        if (account == null || account.length() == 0) {
            title.setText(account);
        }

        if (mFilesAdapter.mFiles == null || mFilesAdapter.mFiles.size() == 0) {

            if (mDriveServiceHelper != null) {
                Log.d(TAG, "Querying for files.");

                mDialog.setMessage("Loading");
                mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Log.d(TAG, "cancel in dialog");
                        mDialog.dismiss();
                        cts.cancel();
                    }
                });
                mDialog.show();

                cts = new CancellationTokenSource();
                mDriveServiceHelper.queryFiles(mPath, cts.getToken())
                .addOnSuccessListener(fileList -> {
                    if (fileList != null) {
                        mFilesAdapter.setFiles(fileList.getFiles());
                    }

                    mDialog.dismiss();
                })
                .addOnFailureListener(exception -> Log.e(TAG, "Unable to query files.", exception));
            }
        }
    }

    public static boolean writeBytesToFile(File file, byte[] data) {
        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(data);
            stream.close();
            return true;
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        return false;
    }

    class FileListAdapter extends BaseAdapter {
        private List mFiles;

        public void setFiles(List files) {
            mFiles = Collections.unmodifiableList(new ArrayList<>(files));
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mFiles == null ? 0 : mFiles.size();
        }

        @Override
        public Object getItem(int position) {
            return mFiles == null ? null : mFiles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            ViewHolder holder = null;

            if (convertView == null) {
                holder = new ViewHolder();

                convertView = inflater.inflate(R.layout.licence_data_row, parent, false);

                holder.mainText = (TextView)convertView.findViewById(R.id.mainText);
                holder.subText = (TextView)convertView.findViewById(R.id.subText);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }

            File item = (File)mFiles.get(position);
            String name = item.getName();
            holder.mainText.setText(name);

            return convertView;
        }
    }

    static class ViewHolder {
        TextView mainText, subText;
    }

    private void viewFileInExternalApp(File result) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String ext = result.getName().substring(result.getName().indexOf(".") + 1);
        String type = mime.getMimeTypeFromExtension(ext);

        Uri tempUri = FileProvider.getUriForFile(this,
                      "com.example.android.fileprovider",
                      result);
        intent.setDataAndType(tempUri, type);

        // 외부에서 해당 URI를 접근할 수 있도록 한다.
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Check for a handler first to avoid a crash
        Intent chooser = Intent.createChooser(intent, "Open File");

        try {
            startActivity(chooser);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here, or something
        }
    }
}

// DriveServiceHelper.java

public class DriveServiceHelper {
    private final Executor mExecutor = Executors.newFixedThreadPool(5);
    private final Drive mDriveService;

    public DriveServiceHelper(Drive driveService) {
        mDriveService = driveService;
    }

    /**
     * Creates a text file in the user's My Drive folder and returns its file ID.
     */
    public Task<String> createFile() {
        return Tasks.call(mExecutor, new Callable<String>() {
            @Override
            public String call() throws Exception {
                File metadata = new File()
                .setParents(Collections.singletonList("root"))
                .setMimeType("text/plain")
                .setName("Untitled file");

                File googleFile = mDriveService.files().create(metadata).execute();

                if (googleFile == null) {
                    throw new IOException("Null result when requesting file creation.");
                }

                return googleFile.getId();

            }
        });
    }

    /**
     * Opens the file identified by {@code fileId} and returns a {@link Pair} of its name and
     * contents.
     */
    public Task<Pair<String, byte[]>> readDocsFile(final String fileId, CancellationToken token) {
        final TaskCompletionSource<Pair<String, byte[]>> tcs = new TaskCompletionSource<>(token);

        mExecutor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    File metadata = mDriveService.files().get(fileId).execute();
                    String name = metadata.getName();
                    Drive.Files.Export export = mDriveService.files().export(fileId, "application/pdf");

                    HttpRequest request = export.buildHttpRequest();
                    request.setConnectTimeout(6 * 30000);
                    request.setReadTimeout(6 * 30000);
                    HttpResponse response = request.execute();

                    if (!tcs.getTask().isComplete()) {
                        try(InputStream is = response.getContent();
                                ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream()) {

                            int bufferSize = 1024;
                            byte[] buffer = new byte[bufferSize];

                            // we need to know how may bytes were read to write them to the byteBuffer
                            int len = 0;

                            while ((len = is.read(buffer)) != -1 && !tcs.getTask().isComplete()) {
                                byteBuffer.write(buffer, 0, len);
                            }


                            Log.d("DriveServiceHelper", "here readdocs??");

                            if (!tcs.getTask().isComplete()) {
                                tcs.setResult(Pair.create(name + ".pdf", byteBuffer.toByteArray()));
                            }
                        }
                    } else {
                        Log.d("DriveServiceHelper", "here disconnect??");
                        response.disconnect();
                    }
                } catch (IOException e) {
                    tcs.setException(e);
                }
            }
        });

        return tcs.getTask();
    }

    /**
     * Opens the file identified by {@code fileId} and returns a {@link Pair} of its name and
     * contents.
     */
    public Task<Pair<String, byte[]>> readFile(final String fileId, CancellationToken token) {

        final TaskCompletionSource<Pair<String, byte[]>> tcs = new TaskCompletionSource<>(token);

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    File metadata = mDriveService.files().get(fileId).execute();
                    String name = metadata.getName();

                    // Stream the file contents to a String.
                    try (InputStream is = mDriveService.files().get(fileId).executeMediaAsInputStream();
                             ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream()) {
                        int bufferSize = 1024;
                        byte[] buffer = new byte[bufferSize];

                        // we need to know how may bytes were read to write them to the byteBuffer
                        int len = 0;

                        while ((len = is.read(buffer)) != -1 && !tcs.getTask().isComplete()) {
                            byteBuffer.write(buffer, 0, len);
                        }

                        if (!tcs.getTask().isComplete()) {
                            tcs.setResult(Pair.create(name, byteBuffer.toByteArray()));
                        }
                    }
                } catch (IOException e) {
                    tcs.setException(e);
                    e.printStackTrace();
                }

            }
        });

        return tcs.getTask();
    }

    /**
     * Updates the file identified by {@code fileId} with the given {@code name} and {@code
     * content}.
     */
    public Task<Void> saveFile(final String fileId, final String name, final String content) {
        return Tasks.call(mExecutor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Create a File containing any metadata changes.
                File metadata = new File().setName(name);

                // Convert content to an AbstractInputStreamContent instance.
                ByteArrayContent contentStream = ByteArrayContent.fromString("text/plain", content);

                // Update the metadata and contents.
                mDriveService.files().update(fileId, metadata, contentStream).execute();
                return null;
            }
        });
    }

    /**
     * Returns a {@link FileList} containing all the visible files in the user's My Drive.
     *
     * <p>The returned list will only contain files visible to this app, i.e. those which were
     * created by this app. To perform operations on files not created by the app, the project must
     * request Drive Full Scope in the <a href="https://play.google.com/apps/publish">Google
     * Developer's Console</a> and be submitted to Google for verification.</p>
     */
    public Task<FileList> queryFiles(final String path, CancellationToken token) {
        token.onCanceledRequested(new OnTokenCanceledListener() {
            @Override
            public void onCanceled() {
                Log.d("DriveServiceHelper", "cancel requests.");
            }
        });
        final TaskCompletionSource<FileList> tcs = new TaskCompletionSource<>(token);

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    FileList list = mDriveService.files().list()
                                    .setFields("kind,nextPageToken,files(mimeType,id,kind,name,webViewLink,thumbnailLink,trashed)")
                                    .setQ("trashed = false and '" + path + "' IN parents").execute();

                    if (!tcs.getTask().isComplete()) {
                        tcs.setResult(list);
                    }
                } catch (IOException e) {
                    tcs.setResult(null);
                    e.printStackTrace();
                }
            }
        });

        return tcs.getTask();
    }

    /**
     * Returns an {@link Intent} for opening the Storage Access Framework file picker.
     */
    public Intent createFilePickerIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        return intent;
    }

    /**
     * Opens the file at the {@code uri} returned by a Storage Access Framework {@link Intent}
     * created by {@link #createFilePickerIntent()} using the given {@code contentResolver}.
     */
    public Task<Pair<String, String>> openFileUsingStorageAccessFramework(
    final ContentResolver contentResolver, final Uri uri) {
        return Tasks.call(mExecutor, new Callable<Pair<String, String>>() {
            @Override
            public Pair<String, String> call() throws Exception {
                String name;

                try (Cursor cursor = contentResolver.query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        name = cursor.getString(nameIndex);
                    } else {
                        throw new IOException("Empty cursor returned for file.");
                    }
                }

                // Read the document's contents as a String.
                String content;

                try (InputStream is = contentResolver.openInputStream(uri);
                         BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    content = stringBuilder.toString();
                }

                return Pair.create(name, content);
            }
        });
    }
}
