import java.util.Random;
public class RandomizedQuickSort {
    public static void sort(int[] arr, Random rand) {
        if (arr == null || arr.length <= 1) return;
        quickSort(arr, 0, arr.length - 1, rand);
    }
    private static void quickSort(int[] arr, int low, int high, Random rand) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high, rand);
            quickSort(arr, low, pivotIndex - 1, rand);
            quickSort(arr, pivotIndex + 1, high, rand);
        }
    }
    private static int partition(int[] arr, int low, int high, Random rand) {
        int randomPivot = low + rand.nextInt(high - low + 1);
        swap(arr, randomPivot, high);
        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }
    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
    public static void deterministicSort(int[] arr) {
        if (arr == null || arr.length <= 1) return;
        detQuickSort(arr, 0, arr.length - 1);
    }
    private static void detQuickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pivot = arr[high];
            int i = low - 1;
            for (int j = low; j < high; j++) {
                if (arr[j] <= pivot) {
                    i++;
                    swap(arr, i, j);
                }
            }
            swap(arr, i + 1, high);
            int p = i + 1;
            detQuickSort(arr, low, p - 1);
            detQuickSort(arr, p + 1, high);
        }
    }
}
