<template>
    <!-- 图表容器必须指定宽高 -->
    <div ref="chartRef" style="width: 100%; height: 400px;"></div>
</template>

<script setup>
    import axios from 'axios'
    import {reactive,ref,onMounted} from 'vue'
    // 完整引入 echarts
    import * as echarts from 'echarts'

    const data = reactive({
        form:{
            userId: '',
            year: 2026
        },
        records: [],
        xData: [],
        yData: []

    })
    data.form.userId = JSON.parse(sessionStorage.getItem("user")).id

    let path = 'http://localhost:8080/orders/selByMonth'
    const getData = ()=>{
        axios.post(path,data.form).then(res=>{
            data.records = res.data
            data.records.forEach(item=>{
                data.xData.push(item.month+'月')
                data.yData.push(item.price)
            })
            renderChart()
        })
    }

    const chartRef = ref()
    // 渲染图表
    const renderChart = () => {
        // 实例化
        let myChart = echarts.init(chartRef.value)
        // 配置项
        const option = {
            title: { text: `${data.form.year}年订单统计` },
            xAxis: {
                type: 'category',
                data: data.xData
            },
            yAxis: { type: 'value' },
            series: [
                {
                    name: '销量',
                    type: 'bar',
                    data: data.yData
                }
            ]
        }
        // 设置配置
        myChart.setOption(option)
    }
    onMounted(()=>{
        getData()
    })

</script>

<style scoped>

</style>