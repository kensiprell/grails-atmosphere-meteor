import grails.test.AbstractCliTestCase

class UpdateAtmosphereJavascriptTests extends AbstractCliTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testUpdateAtmosphereJavascript() {

        execute(["update-atmosphere-javascript"])

        assertEquals 0, waitForProcess()
        verifyHeader()
    }
}
