import { NativeModules } from 'react-native';
import { LINKING_ERROR } from './constant';

const { DJISDKMissionWrapper } = NativeModules;

if (!DJISDKMissionWrapper) {
  throw new Error(LINKING_ERROR);
}

export class SDKMission {
  async getCurrentState(): Promise<any> {
    const currentState = await DJISDKMissionWrapper.getCurrentState();
    return currentState;
  }
}
