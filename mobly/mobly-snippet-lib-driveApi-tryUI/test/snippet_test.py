from mobly import base_test
from mobly import test_runner
from mobly.controllers import android_device

class HelloWorldTest(base_test.BaseTestClass):
    def setup_class(self):
        self.ads = self.register_controller(android_device)
        self.dut = self.ads[0]
        self.dut.load_snippet('snippet', 'com.google.android.mobly.snippet.example2')
        self.dut.snippet.startMainActivity()
        input()

    def test_1_addFile(self):
        self.dut.snippet.addFile()

if __name__ == '__main__':
    test_runner.main()
