import Vue from 'vue';

/**
 * EventBus for cross-component communication
 * 
 * Events:
 * - 'points-updated': Emitted when user points are updated (e.g., after point grant/deduct)
 */
export const EventBus = new Vue();

// Event names as constants
export const EVENTS = {
  POINTS_UPDATED: 'points-updated',
};
