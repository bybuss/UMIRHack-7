package bob.colbaskin.umirhack7.soil_analyze.utils

import bob.colbaskin.umirhack7.soil_analyze.domain.models.SoilAnalysisData

object SoilAnalysisValidator {

    data class ValidationResult(
        val isValid: Boolean,
        val errors: Map<String, String> = emptyMap()
    )

    fun validate(data: SoilAnalysisData): ValidationResult {
        val errors = mutableMapOf<String, String>()

        if (data.N < 0) {
            errors["N"] = "Содержание азота не может быть отрицательным"
        } else if (data.N > 1000) {
            errors["N"] = "Содержание азота слишком высокое (макс. 1000 мг/кг)"
        } else if (data.N == 0.0) {
            errors["N"] = "Введите содержание азота"
        }

        if (data.P < 0) {
            errors["P"] = "Содержание фосфора не может быть отрицательным"
        } else if (data.P > 500) {
            errors["P"] = "Содержание фосфора слишком высокое (макс. 500 мг/кг)"
        } else if (data.P == 0.0) {
            errors["P"] = "Введите содержание фосфора"
        }

        if (data.K < 0) {
            errors["K"] = "Содержание калия не может быть отрицательным"
        } else if (data.K > 2000) {
            errors["K"] = "Содержание калия слишком высокое (макс. 2000 мг/кг)"
        } else if (data.K == 0.0) {
            errors["K"] = "Введите содержание калия"
        }

        if (data.Temperature < -50 || data.Temperature > 100) {
            errors["Temperature"] = "Температура должна быть в диапазоне от -50°C до 100°C"
        } else if (data.Temperature == 0.0) {
            errors["Temperature"] = "Введите температуру почвы"
        }

        if (data.Humidity < 0 || data.Humidity > 100) {
            errors["Humidity"] = "Влажность должна быть в диапазоне от 0% до 100%"
        } else if (data.Humidity == 0.0) {
            errors["Humidity"] = "Введите влажность почвы"
        }

        if (data.pH < 0 || data.pH > 14) {
            errors["pH"] = "Уровень pH должен быть в диапазоне от 0 до 14"
        } else if (data.pH == 0.0) {
            errors["pH"] = "Введите уровень pH"
        }

        if (data.RainFall < 0) {
            errors["RainFall"] = "Количество осадков не может быть отрицательным"
        } else if (data.RainFall > 10000) {
            errors["RainFall"] = "Количество осадков слишком высокое (макс. 10000 мм)"
        } else if (data.RainFall == 0.0) {
            errors["RainFall"] = "Введите количество осадков"
        }

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }
}
