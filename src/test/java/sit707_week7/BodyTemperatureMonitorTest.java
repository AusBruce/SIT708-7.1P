package sit707_week7;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class BodyTemperatureMonitorTest {

    @Test
    public void testStudentIdentity() {
        String studentId = "s223140522";
        Assert.assertNotNull("Student ID is null", studentId);
    }

    @Test
    public void testStudentName() {
        String studentName = "yuheng wang";
        Assert.assertNotNull("Student name is null", studentName);
    }
    
    @Test
    public void testReadTemperatureNegative() {
        TemperatureSensor mockSensor = Mockito.mock(TemperatureSensor.class);
        Mockito.when(mockSensor.readTemperatureValue()).thenReturn(-1.0);
        BodyTemperatureMonitor monitor = new BodyTemperatureMonitor(mockSensor, null, null);
        double temperature = monitor.readTemperature();
        Assert.assertTrue(temperature < 0);
    }

    @Test
    public void testReadTemperatureZero() {
        TemperatureSensor mockSensor = Mockito.mock(TemperatureSensor.class);
        Mockito.when(mockSensor.readTemperatureValue()).thenReturn(0.0);
        BodyTemperatureMonitor monitor = new BodyTemperatureMonitor(mockSensor, null, null);
        double temperature = monitor.readTemperature();
        Assert.assertEquals(0.0, temperature, 0.01);
    }

    @Test
    public void testReadTemperatureNormal() {
        TemperatureSensor mockSensor = Mockito.mock(TemperatureSensor.class);
        Mockito.when(mockSensor.readTemperatureValue()).thenReturn(36.5);
        BodyTemperatureMonitor monitor = new BodyTemperatureMonitor(mockSensor, null, null);
        double temperature = monitor.readTemperature();
        Assert.assertEquals(36.5, temperature, 0.01);
    }

    @Test
    public void testReadTemperatureAbnormallyHigh() {
        TemperatureSensor mockSensor = Mockito.mock(TemperatureSensor.class);
        Mockito.when(mockSensor.readTemperatureValue()).thenReturn(42.0);
        BodyTemperatureMonitor monitor = new BodyTemperatureMonitor(mockSensor, null, null);
        double temperature = monitor.readTemperature();
        Assert.assertTrue(temperature > 41.0);
    }

    @Test
    public void testReportTemperatureReadingToCloud() {
        TemperatureSensor mockSensor = Mockito.mock(TemperatureSensor.class);
        CloudService mockCloud = Mockito.mock(CloudService.class);
        NotificationSender mockSender = Mockito.mock(NotificationSender.class);
        
        Mockito.when(mockSensor.readTemperatureValue()).thenReturn(36.5);
        TemperatureReading tempReading = new TemperatureReading(mockSensor.readTemperatureValue());
        
        BodyTemperatureMonitor monitor = new BodyTemperatureMonitor(mockSensor, mockCloud, mockSender);
        monitor.reportTemperatureReadingToCloud(tempReading);
        
        Mockito.verify(mockCloud).sendTemperatureToCloud(tempReading);
    }

    @Test
    public void testInquireBodyStatusNormalNotification() {
        TemperatureSensor mockSensor = Mockito.mock(TemperatureSensor.class);
        CloudService mockCloud = Mockito.mock(CloudService.class);
        NotificationSender mockSender = Mockito.mock(NotificationSender.class);
        
        Mockito.when(mockCloud.queryCustomerBodyStatus(Mockito.any())).thenReturn("NORMAL");
        
        BodyTemperatureMonitor monitor = new BodyTemperatureMonitor(mockSensor, mockCloud, mockSender);
        monitor.inquireBodyStatus();
        
        Mockito.verify(mockSender).sendEmailNotification(Mockito.any(Customer.class), Mockito.eq("Thumbs Up!"));
    }

    @Test
    public void testInquireBodyStatusAbnormalNotification() {
        TemperatureSensor mockSensor = Mockito.mock(TemperatureSensor.class);
        CloudService mockCloud = Mockito.mock(CloudService.class);
        NotificationSender mockSender = Mockito.mock(NotificationSender.class);
        
        Mockito.when(mockCloud.queryCustomerBodyStatus(Mockito.any())).thenReturn("ABNORMAL");
        
        BodyTemperatureMonitor monitor = new BodyTemperatureMonitor(mockSensor, mockCloud, mockSender);
        monitor.inquireBodyStatus();
        
        Mockito.verify(mockSender).sendEmailNotification(Mockito.any(FamilyDoctor.class), Mockito.eq("Emergency!"));
    }
}
