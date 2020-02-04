package com.github.uiautomator.compat;

//
// Decompiled by Procyon v0.5.30
// Copied from: https://github.com/mzj21/Vysor-Research/blob/master/app/src/main/java/android/view/IRotationWatcher.java
//

import android.os.Parcel;
import android.os.IBinder;
import android.os.Binder;
import android.os.RemoteException;
import android.os.IInterface;

public interface IRotationWatcher extends IInterface {
    void onRotationChanged(final int p0) throws RemoteException;

    public abstract static class Stub extends Binder implements IRotationWatcher {
        private static final String DESCRIPTOR = "android.view.IRotationWatcher";
        static final int TRANSACTION_onRotationChanged = 1;

        public Stub() {
            attachInterface(this, "android.view.IRotationWatcher");
        }

        public static IRotationWatcher asInterface(final IBinder binder) {
            IRotationWatcher rotationWatcher;
            if (binder == null) {
                rotationWatcher = null;
            } else {
                final IInterface queryLocalInterface = binder.queryLocalInterface("android.view.IRotationWatcher");
                if (queryLocalInterface != null && queryLocalInterface instanceof IRotationWatcher) {
                    rotationWatcher = (IRotationWatcher) queryLocalInterface;
                } else {
                    rotationWatcher = new Proxy(binder);
                }
            }
            return rotationWatcher;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(final int n, final Parcel parcel, final Parcel parcel2, final int n2) throws RemoteException {
            boolean onTransact = true;
            switch (n) {
                default: {
                    onTransact = super.onTransact(n, parcel, parcel2, n2);
                    break;
                }
                case 1598968902: {
                    parcel2.writeString("android.view.IRotationWatcher");
                    break;
                }
                case 1: {
                    parcel.enforceInterface("android.view.IRotationWatcher");
                    this.onRotationChanged(parcel.readInt());
                    break;
                }
            }
            return onTransact;
        }

        private static class Proxy implements IRotationWatcher {
            private IBinder mRemote;

            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return "android.view.IRotationWatcher";
            }

            @Override
            public void onRotationChanged(final int n) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.view.IRotationWatcher");
                    obtain.writeInt(n);
                    this.mRemote.transact(1, obtain, (Parcel) null, 1);
                } finally {
                    obtain.recycle();
                }
            }
        }
    }
}
