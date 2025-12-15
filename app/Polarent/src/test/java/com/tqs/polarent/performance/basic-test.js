import http from 'k6/http';
import { check, sleep } from 'k6';

// This is the configuration for the test.
export const options = {
  vus: 10, // Number of virtual users
  duration: '30s', // Duration of the test
};

// The main function that contains the logic for each virtual user.
// k6 will execute this function in a loop for the duration of the test.
export default function () {
  const res = http.get('http://localhost:8080/api/listings/enabled');

  // Check if the request was successful (HTTP status 200)
  check(res, { 'status was 200': (r) => r.status == 200 });

  // Wait for 1 second before making another request.
  sleep(1);
}