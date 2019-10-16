package com.utsman.smm;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

public class Smm {

    public static void subscribe(final Context context, final String topic, final DataListener dataListener) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (info.metaData != null) {
                final String server = info.metaData.getString("com.utsman.smm.SERVER");
                String username = info.metaData.getString("com.utsman.smm.USERNAME");
                String password = info.metaData.getString("com.utsman.smm.PASSWORD");
                final String clientId = info.metaData.getString("com.utsman.smm.CLIENT_ID");
                Log.i("anjay", "metadata nya adalah " + server);

                final MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
                mqttConnectOptions.setAutomaticReconnect(true);
                mqttConnectOptions.setCleanSession(true);
                mqttConnectOptions.setUserName(username);
                mqttConnectOptions.setPassword(password != null ? password.toCharArray() : new char[0]);

                connect(context, topic, dataListener, server, clientId, mqttConnectOptions);
            } else {
                Log.e("not metadata", "Check your manifest");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e("not metadata", "Check your manifest");
        }
    }

    private static void connect(Context context, final String topic, final DataListener dataListener, String server, final String clientId, MqttConnectOptions mqttConnectOptions) {
        try {
            final MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(context, server, clientId, new MemoryPersistence());

            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i("anjay", "onSuccess: connect successfully");

                    try {
                        mqttAndroidClient.subscribe(topic, 2, 2, new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                Log.i("anjay", "onSuccess: subscribe successfully");

                                mqttAndroidClient.setCallback(new MqttCallback() {
                                    @Override
                                    public void connectionLost(Throwable cause) {
                                        Log.e("anjay", "Connection lost, maybe duplicate clientId when connected");
                                    }

                                    @Override
                                    public void messageArrived(String topicReceiver, org.eclipse.paho.client.mqttv3.MqttMessage message) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(new String(message.getPayload()));
                                            String clientIdJson = jsonObject.getString("clientId");
                                            JSONObject data = jsonObject.getJSONObject("data");

                                            Log.i("anjay", "onSuccess: message complete from --> " + clientId);
                                            dataListener.onListenData(clientIdJson, data);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        Log.i("anjay", "onSuccess: message arrived " + new String(message.getPayload()));
                                    }

                                    @Override
                                    public void deliveryComplete(IMqttDeliveryToken token) {
                                        Log.i("anjay", "onSuccess: message deliveriy");
                                    }
                                });
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                Log.e("anjay", "onFailure: subscribe", exception.fillInStackTrace());

                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        Log.e("not metadata", "Check your manifest");
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("anjay", "Check your manifest maybe server is error");
                    Log.e("anjay", "onFailure: ", exception.fillInStackTrace());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            Log.e("not metadata", "Check your manifest");
        }
    }

    public static void publish(final Context context, final String topic, final JSONObject data) {

        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (info.metaData != null) {
                String server = info.metaData.getString("com.utsman.smm.SERVER");
                String username = info.metaData.getString("com.utsman.smm.USERNAME");
                String password = info.metaData.getString("com.utsman.smm.PASSWORD");
                final String clientId = info.metaData.getString("com.utsman.smm.CLIENT_ID");

                final MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
                mqttConnectOptions.setAutomaticReconnect(true);
                mqttConnectOptions.setCleanSession(true);
                mqttConnectOptions.setUserName(username);
                mqttConnectOptions.setPassword(password != null ? password.toCharArray() : new char[0]);

                final MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(context, server, clientId + "_sender", new MemoryPersistence());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("clientId", clientId);
                jsonObject.put("data", data);

                final byte[] bytes = String.valueOf(jsonObject).getBytes();
                final org.eclipse.paho.client.mqttv3.MqttMessage mqttMessage = new org.eclipse.paho.client.mqttv3.MqttMessage();
                mqttMessage.setPayload(bytes);

                try {
                    mqttAndroidClient.connect(mqttConnectOptions, 1, new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            try {
                                mqttAndroidClient.publish(topic, bytes, 2, false, 1, new IMqttActionListener() {
                                    @Override
                                    public void onSuccess(IMqttToken asyncActionToken) {
                                        Log.i("anjay", "publish onSuccess: message complete " + asyncActionToken.toString());

                                        try {
                                            mqttAndroidClient.disconnect(asyncActionToken.getUserContext(), new IMqttActionListener() {
                                                @Override
                                                public void onSuccess(IMqttToken asyncActionToken) {
                                                    Log.i("anjay", "onSuccess: disconnect success");
                                                }

                                                @Override
                                                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                                    Log.e("anjay", "onFailure: disconnect failure");
                                                }
                                            });
                                        } catch (MqttException e) {
                                            e.printStackTrace();
                                        } catch (IllegalArgumentException e) {
                                            Log.e("anjay", "onSuccess: anjay gagal", e.fillInStackTrace());
                                        }
                                    }

                                    @Override
                                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                        Log.e("anjay", "onFailure: subscribe", exception.fillInStackTrace());
                                    }
                                });


                            } catch (MqttException e) {
                                e.printStackTrace();
                                Log.e("anjay", "onFailure: subscribe", e.fillInStackTrace());
                            }
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            exception.printStackTrace();
                            Log.e("not metadata", "Check your manifest");
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                    Log.e("not metadata", "Check your manifest");
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
