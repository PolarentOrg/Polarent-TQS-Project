import http from 'k6/http';
import { check, sleep } from 'k6';

// The options object now includes 'thresholds', which define the SLOs for this test.
export const options = {
  vus: 10, // Number of virtual users
  duration: '30s', // Duration of the test
  thresholds: {
    //The rate of failed requests must be less than 1%.
    http_req_failed: ['rate<0.01'],

    // 95% of requests must complete within 500ms.
    http_req_duration: ['p(95)<500'],
  },
};

// The main function remains the same. It's executed by each virtual user.
export default function () {
  const res = http.get('http://localhost:8080/api/listings/enabled');

  // Check if the request was successful. This is important for the 'http_req_failed' threshold.
  check(res, { 'status was 200': (r) => r.status == 200 });

  sleep(1);
}