package com.example.sample;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertTrue;

public class MockitoExampleTest {

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    MyDatabase databaseMock;

    @Test
    public void testWhenListIsNull_queryWillBeTrue()  {
        final ClassToTest t  = new ClassToTest(databaseMock);
        Mockito.when(databaseMock.query("* from t")).thenReturn(null);

        final boolean check = t.query("* from t");

        assertTrue(check);
        Mockito.verify(databaseMock).query("* from t");
    }

}
