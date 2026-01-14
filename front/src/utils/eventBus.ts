import Vue from 'vue';

/**
 * EventBus for cross-component communication
 * 
 * Events:
 * - 'points-updated': Emitted when user points are updated (e.g., after point grant/deduct)
 * - 'open-login-modal': Emitted when login modal should be opened from any component
 */
export const EventBus = new Vue();

// Event names as constants
export const EVENTS = {
  POINTS_UPDATED: 'points-updated',
  OPEN_LOGIN_MODAL: 'open-login-modal',
};
