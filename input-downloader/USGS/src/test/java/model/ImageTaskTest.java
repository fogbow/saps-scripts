package model;

import model.ImageTask;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.TestCase.fail;

public class ImageTaskTest {

    @Test
    public void testImageNameBuilder(){

        ImageTask imageTask = new ImageTask("landsat_5", "215065", "1984-01-01");
        Assert.assertEquals("LT52150651984001CUB00", imageTask.buildImageName());

        imageTask = new ImageTask("landsat_7", "215065", "2017-12-31");
        Assert.assertEquals("LE72150652017365CUB00", imageTask.buildImageName());

        imageTask = new ImageTask("landsat_7", "215065", "2017-02-28");
        Assert.assertEquals("LE72150652017059CUB00", imageTask.buildImageName());

        imageTask = new ImageTask("landsat_8", "215065", "2017-05-31");
        Assert.assertEquals("LC82150652017151CUB00", imageTask.buildImageName());

        try {
            imageTask = new ImageTask("landsat_7", "215065", "2017-00-12");
            fail();
        } catch (Exception e){
            Assert.assertEquals(IllegalArgumentException.class, e.getClass());
            Assert.assertEquals("MONTH", e.getMessage());
        }

        try {
            imageTask = new ImageTask("landsat_7", "215065", "2017-02-29");
            fail();
        } catch (Exception e){
            Assert.assertEquals(IllegalArgumentException.class, e.getClass());
        }

        try {
            imageTask = new ImageTask("landsat_7", "215065", "2017-12-32");
            fail();
        } catch (Exception e){
            Assert.assertEquals(IllegalArgumentException.class, e.getClass());
            Assert.assertEquals("DAY_OF_MONTH", e.getMessage());
        }
    }
}
