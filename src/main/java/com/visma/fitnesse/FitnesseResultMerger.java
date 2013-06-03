package com.visma.fitnesse;

import com.visma.fitnesse.schema.ObjectFactory;
import com.visma.fitnesse.schema.TestResults;
import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * Hello world!
 *
 */
public class FitnesseResultMerger 
{
    private static final String OUTPUT_ARG = "-output:";
    private static JAXBContext jAXBContext;
    private static Unmarshaller jaxbUnmarshaller;
    private static Marshaller jaxbMarshaller;
    
    private static TestResults mergedResults;

    public static void main( String[] args ) throws JAXBException
    {
        jAXBContext = JAXBContext.newInstance(TestResults.class);
        jaxbUnmarshaller = jAXBContext.createUnmarshaller();
        jaxbMarshaller = jAXBContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        mergedResults = new ObjectFactory().createTestResults();
        mergedResults.setFinalCounts(new ObjectFactory().createTestResultsFinalCounts());
        String outputFile = null;
        for(String arg: args){
            if (arg.startsWith(OUTPUT_ARG)){
                outputFile = arg.replace(OUTPUT_ARG, "");
                break;
            }
            TestResults testResults = parseFile(arg);
            // toString(testResults);
            mergeResult(testResults);
        }
        // toString(mergedResults);
        if (outputFile == null){
            jaxbMarshaller.marshal(mergedResults, System.out);        
        }
        else{
            File out = new File(outputFile);
            jaxbMarshaller.marshal(mergedResults, out);
        }
    }
    
    private static TestResults parseFile(String fileName) throws JAXBException{
        File file = new File(fileName);
        return (TestResults)jaxbUnmarshaller.unmarshal(file);
    }
    
    private static void mergeResult(TestResults result){
        mergedResults.setFitNesseVersion(result.getFitNesseVersion());
        mergedResults.getResult().addAll(result.getResult());
        mergedResults.setTotalRunTimeInMillis(mergedResults.getTotalRunTimeInMillis() + result.getTotalRunTimeInMillis());
        mergedResults.setRootPath( mergedResults.getRootPath() == null ? result.getRootPath() : mergedResults.getRootPath() + ", " + result.getRootPath());
        mergeFinalCounts(result.getFinalCounts());
    }
    
    private static void mergeFinalCounts(TestResults.FinalCounts finalCounts){
        TestResults.FinalCounts mergedFinalCounts = mergedResults.getFinalCounts();
        mergedFinalCounts.setExceptions(mergedFinalCounts.getExceptions() + finalCounts.getExceptions());
        mergedFinalCounts.setIgnores(mergedFinalCounts.getIgnores()+ finalCounts.getIgnores());
        mergedFinalCounts.setRight(mergedFinalCounts.getRight()+ finalCounts.getRight());
        mergedFinalCounts.setWrong(mergedFinalCounts.getWrong()+ finalCounts.getWrong());
    }
    
    private static void toString(TestResults testResults){
        System.out.println("===== rootPath: " + testResults.getRootPath());
        System.out.println("totalRunTimeMillis: " + testResults.getTotalRunTimeInMillis());            
        System.out.println("finalCounts.right: " + testResults.getFinalCounts().getRight());            
        System.out.println("finalCounts.wrong: " + testResults.getFinalCounts().getWrong());            
        System.out.println("finalCounts.exceptions: " + testResults.getFinalCounts().getExceptions());            
        System.out.println("finalCounts.ignores: " + testResults.getFinalCounts().getIgnores());            
    }
}
