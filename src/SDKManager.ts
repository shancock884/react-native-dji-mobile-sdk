import { NativeModules, PermissionsAndroid, Platform } from 'react-native';
import { LINKING_ERROR } from './constant';
import { SDKAircraft } from './SDKAircraft';
import { SDKMission } from './SDKMission';
import { onceProductConnected } from './utils';

const { DJISDKManagerWrapper } = NativeModules;

if (!DJISDKManagerWrapper) {
  throw new Error(LINKING_ERROR);
}

export class SDKManager {
  SDKRegistered = false;
  #product: SDKAircraft | undefined;
  #mission: SDKMission | undefined;

  getSDKVersion = async (): Promise<string> => {
    return await DJISDKManagerWrapper.getSDKVersion();
  };

  startConnectionToProduct = async (): Promise<void> => {
    return new Promise(async (resolve, reject) => {
      let connectTO: NodeJS.Timeout;
      const sub = onceProductConnected(() => {
        this.#product = new SDKAircraft();
        clearTimeout(connectTO);
        resolve();
      });
      connectTO = setTimeout(() => {
        reject(new Error('Connection Product timeout'));
        sub.remove();
      }, 10000);
      await DJISDKManagerWrapper.startConnectionToProduct();
    });
  };

  registerApp = async () => {
    if (Platform.OS === 'android') {
      console.log([
        PermissionsAndroid.PERMISSIONS.ACCESS_COARSE_LOCATION!,
        PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION!,
        PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE!,
        PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE!,
        PermissionsAndroid.PERMISSIONS.READ_PHONE_STATE!,
      ]);
      const granted = await PermissionsAndroid.requestMultiple([
        PermissionsAndroid.PERMISSIONS.ACCESS_COARSE_LOCATION!,
        PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION!,
        PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE!,
        PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE!,
        PermissionsAndroid.PERMISSIONS.READ_PHONE_STATE!,
      ]);
      Object.entries(granted).map(([key, values]) => {
        if (values !== 'granted') {
          throw new Error(`Permission not granted for ${key}`);
        }
      });
      await DJISDKManagerWrapper.registerApp();
      this.SDKRegistered = true;
    }
  };

  getProduct = async () => {
    if (!this.SDKRegistered) {
      throw new Error('SDK not registered');
    }
    if (!this.#product) {
      throw new Error('Product not registered');
    }
    return this.#product;
  };

  getMission = async () => {
    if (!this.SDKRegistered) {
      throw new Error('SDK not registered');
    }
    if (!this.#mission) {
      this.#mission = new SDKMission();
    }
    return this.#mission;
  };
}

export const sdkManager = new SDKManager();
