# PermissionHelper
The easiest way to add support for run-time permission in a fragment.



**1st step: Init a permission helper**

    private PermissionHelper permissionHelper;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionHelper = new PermissionHelper();
    }

**2nd step: Give the result onPermissionGranted to the PermissionHelper**
       
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionHelper.onRequestPermissionResult(this, requestCode, permissions[0], grantResults);
    }

**3nd step: Requesting a specific permission inside of a Fragment**

    permissionHelper.executeWithPermCheck(this, PermissionHelper.STORAGE,
                new PermissionHelper.Callback() {
                    @Override
                    public void onPermissionGranted() {
                        // When the permission is granted
                    }

                    @Override
                    public void onRationaleNeeded() {
                        // The case when Don't show again is checked
                    }
                });
                
**The reference to the callback is Weak so if you want to be sure make a strong reference to it.**
                
