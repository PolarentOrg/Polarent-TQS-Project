module.exports = {
  default: {
    paths: ['features/**/*.feature'],
    require: ['steps/**/*.js'],
    format: ['progress', 'html:reports/cucumber-report.html'],
    publishQuiet: true
  }
};
