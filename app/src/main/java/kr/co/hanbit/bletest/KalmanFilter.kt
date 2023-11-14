package kr.co.hanbit.bletest

import kotlin.system.measureNanoTime

class KalmanFilter {
    private val PROCESS_NOISE = 20.0
    private val MEASUREMENT_NOISE = 20.0



    private var isInit = false



    private var predRssi = 0.0
    private var errorCovariance = 0.0



    constructor() {
        isInit = false
        predRssi = 0.0
        errorCovariance = 0.0
    }



    public fun getPredictedRssi(rssi: Int): Int {
        var privRssi = 0.0
        var privErrorCovarinance = 0.0

        if(!isInit) {
            isInit = true
            privRssi = rssi.toDouble()
            privErrorCovarinance = 1.0
        }
        else {
            privRssi = predRssi
            privErrorCovarinance = errorCovariance + PROCESS_NOISE
        }

        val kalmanGain = privErrorCovarinance / (privErrorCovarinance + MEASUREMENT_NOISE)
        predRssi = privRssi + (kalmanGain * (rssi - privRssi))
        errorCovariance = (1 - kalmanGain) * privErrorCovarinance

        return privRssi.toInt()
    }
}
/*
class KalmanFilter():
	def __init__(self, processNoise = 0.005, measurementNoise = 20):
		super(KalmanFilter, self).__init__()
		self.initialized = False
		self.processNoise = processNoise
		self.measurementNoise = measurementNoise
		self.predictedRSSI = 0
		self.errorCovariance = 0

	def filtering(self, rssi):
		if not self.isInitialized:
			self.isInitialized = True
			priorRSSI = rssi
			priorErrorCovariance = 1
		else:
			priorRSSI = self.predictedRSSI
			priorErrorCovariance = self.errorCovariance + self.processNoise

		kalmanGain = priorErrorCovariance / (priorErrorCovariance + self.measurementNoise)
		self.predictedRSSI = priorRSSI + (kalmanGain * (rssi - priorRSSI))
		self.errorCovarianceRSSI = (1 - kalmanGain) * priorErrorCovariance

		return self.predictedRSSI


 */