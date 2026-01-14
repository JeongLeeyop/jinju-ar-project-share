<template>
  <Bar
    :chart-options="chartOptions"
    :chart-data="chartData"
    :chart-id="chartId"
    :dataset-id-key="datasetIdKey"
    :plugins="plugins"
    :css-classes="cssClasses"
    :styles="styles"
    :width="width"
    :height="height"
  />
</template>

<script>
import { Bar } from 'vue-chartjs';
import {
  Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale,
} from 'chart.js';

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale);

export default {
  name: 'BarChart',
  components: { Bar },
  props: {
    chartId: {
      type: String,
      default: 'bar-chart',
    },
    datasetIdKey: {
      type: String,
      default: 'label',
    },
    width: {
      type: Number,
      default: 400,
    },
    height: {
      type: Number,
      default: 400,
    },
    cssClasses: {
      default: '',
      type: String,
    },
    styles: {
      type: Object,
    },
    plugins: {
      type: Object,
    },
  },
  data() {
    return {
      chartData: {
        labels: ['09-24 (토)', '09-25 (일)', '09-26 (월)', '09-27 (화)', '09-28 (수)', '09-29 (목)', '09-30 (금)'],
        datasets: [
          {
            label: '취소',
            backgroundColor: '#63c5ca',
            data: [10, 20, 10, 20, 10, 10, 20],
          },
          {
            label: '주문',
            backgroundColor: '#8460cb',
            data: [30, 50, 30, 50, 30, 50, 50],
          }],
      },
      chartOptions: {
        responsive: true,
        maintainAspectRatio: true,
        aspectRatio: 5,
        scales: {
          y: {
            beginAtZero: true,
            max: 100,
          },
        },
      },
    };
  },
};
</script>
