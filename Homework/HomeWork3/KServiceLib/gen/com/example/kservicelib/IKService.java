/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\Captain-Hook\\workspace\\KServiceLib\\src\\com\\example\\kservicelib\\IKService.aidl
 */
package com.example.kservicelib;
public interface IKService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.example.kservicelib.IKService
{
private static final java.lang.String DESCRIPTOR = "com.example.kservicelib.IKService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.example.kservicelib.IKService interface,
 * generating a proxy if needed.
 */
public static com.example.kservicelib.IKService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.example.kservicelib.IKService))) {
return ((com.example.kservicelib.IKService)iin);
}
return new com.example.kservicelib.IKService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_fibonacci:
{
data.enforceInterface(DESCRIPTOR);
com.example.kservicelib.Request _arg0;
if ((0!=data.readInt())) {
_arg0 = com.example.kservicelib.Request.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
com.example.kservicelib.Response _result = this.fibonacci(_arg0);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.example.kservicelib.IKService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public com.example.kservicelib.Response fibonacci(com.example.kservicelib.Request request) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.example.kservicelib.Response _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((request!=null)) {
_data.writeInt(1);
request.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_fibonacci, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.example.kservicelib.Response.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_fibonacci = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public com.example.kservicelib.Response fibonacci(com.example.kservicelib.Request request) throws android.os.RemoteException;
}
