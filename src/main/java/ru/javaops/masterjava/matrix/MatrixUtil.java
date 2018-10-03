package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        int [][] matrixC = new int[matrixSize][matrixSize];
        final CompletionService<Void> completionService = new ExecutorCompletionService<>(executor);

        int [][] matrixBT = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                matrixBT[j][i] = matrixB[i][j];
            }
        }

        List<Future<Void>> futures = new ArrayList<>();
        final int threadQuantity = MainMatrix.getThreadNumber();
        int step = matrixSize/threadQuantity;
        for (int i = 0; i < threadQuantity;i++) {
            final int index = i * step;
                Future<Void> submit = completionService.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                   for (int i = index; i < index + step; i++) {
                        for (int j = 0; j < matrixSize; j++) {
                            int sum = 0;
                            for (int k = 0; k < matrixSize; k++) {
                                sum += matrixA[i][k] * matrixBT[j][k];;
                            }
                            matrixC[i][j] = sum;
                        }
                    }
                   return null;
                }
            });
            futures.add(submit);
        }

        while (futures.size() != 0){
            Future<Void> future = completionService.poll();
            futures.remove(future);
        }

        return matrixC;
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        int [][] BT = new int[matrixSize][matrixSize];
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                BT[j][i] = matrixB[i][j];
            }
        }

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[i][k] * BT[j][k];;
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

}
